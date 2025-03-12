package com.leverx.ratingsystem.controller;

import com.leverx.ratingsystem.entity.Comment;
import com.leverx.ratingsystem.service.CommentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/users/{sellerId}/comments")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping
    public ResponseEntity<Comment> addComment(@PathVariable Long sellerId, @RequestParam Long authorId, @RequestParam String comment) {
        return ResponseEntity.ok(commentService.addComment(sellerId, authorId, comment));
    }

    @GetMapping
    public ResponseEntity<List<Comment>> getSellerComments(@PathVariable Long sellerId, @RequestParam(defaultValue = "true") boolean onlyApproved) {
        return ResponseEntity.ok(commentService.getSellerComments(sellerId, onlyApproved));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Comment> getCommentById(@PathVariable Long id) {
        Optional<Comment> comment = commentService.getCommentById(id);
        return comment.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<Comment> approveComment(@PathVariable Long id) {
        return ResponseEntity.ok(commentService.approveComment(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id, @RequestParam Long authorId) {
        commentService.deleteComment(id, authorId);
        return ResponseEntity.noContent().build();
    }
}
