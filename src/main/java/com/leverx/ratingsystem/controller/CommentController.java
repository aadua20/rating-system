package com.leverx.ratingsystem.controller;

import com.leverx.ratingsystem.entity.Comment;
import com.leverx.ratingsystem.entity.User;
import com.leverx.ratingsystem.service.CommentService;
import com.leverx.ratingsystem.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.http.ResponseEntity;
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

    @Operation(summary = "Add a comment and rating for a seller")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Comment submitted successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data")
    })
    @PostMapping("/users/{sellerId}/comments")
    public ResponseEntity<Comment> addComment(
            @PathVariable Long sellerId,
            @RequestParam String comment,
            @Parameter(description = "Rating from 1 to 5")
            @RequestParam @Min(1) @Max(5)Integer rating,
            @AuthenticationPrincipal UserDetails userDetails) {

        User author = userService.getAuthenticatedUser(userDetails);
        return ResponseEntity.ok(commentService.addComment(sellerId, author, comment, rating));
    }

    @Operation(summary = "Get all comments for a seller")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Comments retrieved successfully")
    })
    @GetMapping("/users/{sellerId}/comments")
    public ResponseEntity<List<Comment>> getSellerComments(
            @PathVariable Long sellerId,
            @RequestParam(defaultValue = "true") boolean onlyApproved) {
        return ResponseEntity.ok(commentService.getSellerComments(sellerId, onlyApproved));
    }

    @Operation(summary = "Get comment by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Comment found"),
            @ApiResponse(responseCode = "404", description = "Comment not found")
    })
    @GetMapping("comments/{id}")
    public ResponseEntity<Comment> getCommentById(@PathVariable Long id) {
        Optional<Comment> comment = commentService.getCommentById(id);
        return comment.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Delete a comment by ID (if owner)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Comment deleted successfully"),
            @ApiResponse(responseCode = "403", description = "User not authorized to delete comment"),
            @ApiResponse(responseCode = "404", description = "Comment not found")
    })
    @DeleteMapping("comments/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.getAuthenticatedUser(userDetails);
        commentService.deleteComment(id, user);
        return ResponseEntity.noContent().build();
    }
}
