package com.leverx.ratingsystem.service;

import com.leverx.ratingsystem.entity.Comment;
import com.leverx.ratingsystem.entity.Role;
import com.leverx.ratingsystem.entity.User;
import com.leverx.ratingsystem.repository.CommentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserService userService;

    public CommentService(CommentRepository commentRepository, UserService userService) {
        this.commentRepository = commentRepository;
        this.userService = userService;
    }

    public Comment addComment(Long sellerId, User author, String message, Integer rating) {
        User seller = userService.findSeller(sellerId);

        Comment comment = Comment.builder()
                .seller(seller)
                .author(author)
                .message(message)
                .rating(rating)
                .createdAt(LocalDateTime.now())
                .approved(false)
                .build();

        Comment savedComment = commentRepository.save(comment);
        log.info("New comment added by user {} for seller {} (approved: false)", author.getId(), sellerId);
        return savedComment;
    }

    public List<Comment> getSellerComments(Long sellerId, boolean onlyApproved) {
        User seller = userService.findSeller(sellerId);
        return commentRepository.findBySellerAndApproved(seller, onlyApproved);
    }

    public List<Comment> getUnapprovedComments() {
        return commentRepository.findByApproved(false);
    }

    public Optional<Comment> getCommentById(Long id) {
        return commentRepository.findById(id);
    }

    public void approveComment(Long id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        comment.setApproved(true);
        commentRepository.save(comment);
        log.info("Comment {} approved by admin", id);
    }

    public void declineComment(Long id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        commentRepository.delete(comment);
        log.info("Comment {} declined and deleted by admin", id);
    }

    public void deleteComment(Long id, User user) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        boolean isAuthor = comment.getAuthor() != null && user != null && comment.getAuthor().getId().equals(user.getId());
        boolean isAdmin = user != null && Role.ADMIN.equals(user.getRole());

        if (!isAuthor && !isAdmin) {
            throw new RuntimeException("You can only delete your own comments");
        }

        commentRepository.delete(comment);
        log.info("Comment {} deleted by {}", id, isAdmin ? "admin" : "author");
    }
}
