package com.leverx.ratingsystem.controller;

import com.leverx.ratingsystem.entity.Comment;
import com.leverx.ratingsystem.entity.User;
import com.leverx.ratingsystem.service.CommentService;
import com.leverx.ratingsystem.service.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
@SecurityRequirement(name = "BearerAuth")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;
    private final CommentService commentService;

    @PutMapping("/approve_seller/{userId}")
    public ResponseEntity<String> approveSeller(@PathVariable Long userId) {
        userService.approveSeller(userId);
        return ResponseEntity.ok("Seller approved successfully.");
    }

    @DeleteMapping("/decline_seller/{userId}")
    public ResponseEntity<String> declineSeller(@PathVariable Long userId) {
        userService.declineSeller(userId);
        return ResponseEntity.ok("Seller registration declined and removed.");
    }

    @PutMapping("comments/{id}/approve")
    public ResponseEntity<String> approveComment(@PathVariable Long id) {
        commentService.approveComment(id);
        return ResponseEntity.ok("Comment approved successfully.");
    }

    @DeleteMapping("comments/{id}/decline")
    public ResponseEntity<String> declineComment(@PathVariable Long id) {
        commentService.declineComment(id);
        return ResponseEntity.ok("Comment declined and deleted.");
    }

    @GetMapping("/pending_sellers")
    public ResponseEntity<List<User>> getPendingSellers() {
        return ResponseEntity.ok(userService.getUnapprovedSellers());
    }

    @GetMapping("/pending_comments")
    public ResponseEntity<List<Comment>> getPendingComments() {
        return ResponseEntity.ok(commentService.getUnapprovedComments());
    }
}
