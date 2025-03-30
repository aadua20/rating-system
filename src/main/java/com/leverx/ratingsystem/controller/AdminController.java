package com.leverx.ratingsystem.controller;

import com.leverx.ratingsystem.entity.Comment;
import com.leverx.ratingsystem.entity.User;
import com.leverx.ratingsystem.service.CommentService;
import com.leverx.ratingsystem.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @Operation(summary = "Approve seller registration by user ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Seller approved successfully")
    })
    @PutMapping("/approve_seller/{userId}")
    public ResponseEntity<String> approveSeller(@PathVariable Long userId) {
        userService.approveSeller(userId);
        return ResponseEntity.ok("Seller approved successfully.");
    }

    @Operation(summary = "Decline and remove seller registration by user ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Seller registration declined and removed")
    })
    @DeleteMapping("/decline_seller/{userId}")
    public ResponseEntity<String> declineSeller(@PathVariable Long userId) {
        userService.declineSeller(userId);
        return ResponseEntity.ok("Seller registration declined and removed.");
    }

    @Operation(summary = "Approve a comment by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Comment approved successfully")
    })
    @PutMapping("comments/{id}/approve")
    public ResponseEntity<String> approveComment(@PathVariable Long id) {
        commentService.approveComment(id);
        return ResponseEntity.ok("Comment approved successfully.");
    }

    @Operation(summary = "Decline and delete a comment by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Comment declined and deleted")
    })
    @DeleteMapping("comments/{id}/decline")
    public ResponseEntity<String> declineComment(@PathVariable Long id) {
        commentService.declineComment(id);
        return ResponseEntity.ok("Comment declined and deleted.");
    }

    @Operation(summary = "Get list of pending seller registrations")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pending sellers retrieved successfully")
    })
    @GetMapping("/pending_sellers")
    public ResponseEntity<List<User>> getPendingSellers() {
        return ResponseEntity.ok(userService.getUnapprovedSellers());
    }

    @Operation(summary = "Get list of pending comments")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pending comments retrieved successfully")
    })
    @GetMapping("/pending_comments")
    public ResponseEntity<List<Comment>> getPendingComments() {
        return ResponseEntity.ok(commentService.getUnapprovedComments());
    }
}
