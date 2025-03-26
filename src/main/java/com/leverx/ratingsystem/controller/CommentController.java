package com.leverx.ratingsystem.controller;

import com.leverx.ratingsystem.entity.Comment;
import com.leverx.ratingsystem.entity.User;
import com.leverx.ratingsystem.service.CommentService;
import com.leverx.ratingsystem.service.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("")
public class CommentController {

    private final CommentService commentService;
    private final UserService userService;

    public CommentController(CommentService commentService, UserService userService) {
        this.commentService = commentService;
        this.userService = userService;
    }

    @PostMapping("/users/{sellerId}/comments")
    public ResponseEntity<Comment> addComment(
            @PathVariable Long sellerId,
            @RequestParam String comment,
            @RequestParam Integer rating,
            @AuthenticationPrincipal UserDetails userDetails) {

        User author = userService.getAuthenticatedUser(userDetails); // Null if anonymous
        return ResponseEntity.ok(commentService.addComment(sellerId, author, comment, rating));
    }

    @GetMapping("/users/{sellerId}/comments")
    public ResponseEntity<List<Comment>> getSellerComments(
            @PathVariable Long sellerId,
            @RequestParam(defaultValue = "true") boolean onlyApproved) {
        return ResponseEntity.ok(commentService.getSellerComments(sellerId, onlyApproved));
    }

    @GetMapping("comments/{id}")
    public ResponseEntity<Comment> getCommentById(@PathVariable Long id) {
        Optional<Comment> comment = commentService.getCommentById(id);
        return comment.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("comments/{id}/approve")
    @SecurityRequirement(name = "BearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Comment> approveComment(@PathVariable Long id) {
        return ResponseEntity.ok(commentService.approveComment(id));
    }

    @DeleteMapping("comments/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id,  @AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.getAuthenticatedUser(userDetails);
        commentService.deleteComment(id, user);
        return ResponseEntity.noContent().build();
    }
}
