package com.leverx.ratingsystem.service;

import com.leverx.ratingsystem.entity.Comment;
import com.leverx.ratingsystem.entity.User;
import com.leverx.ratingsystem.repository.CommentRepository;
import com.leverx.ratingsystem.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

    public CommentService(CommentRepository commentRepository, UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
    }

    public Comment addComment(Long sellerId,  Long authorId, String message) {
        User seller = userRepository.findById(sellerId).orElseThrow(() -> new RuntimeException("Seller not found"));
        User author = userRepository.findById(authorId).orElseThrow(() -> new RuntimeException("User not found"));

        Comment comment = new Comment();
        comment.setSeller(seller);
        comment.setAuthor(author);
        comment.setMessage(message);
        comment.setApproved(false);

        return commentRepository.save(comment);
    }

    public List<Comment> getSellerComments(Long sellerId, boolean onlyApproved) {
        User seller = userRepository.findById(sellerId).orElseThrow(() -> new RuntimeException("Seller not found"));
        return commentRepository.findBySellerAndApproved(seller, onlyApproved);
    }

    public List<Comment> getUnapprovedComments() {
        return commentRepository.findByApproved(false);
    }

    public Optional<Comment> getCommentById(Long id) {
        return commentRepository.findById(id);
    }

    public Comment approveComment(Long id) {
        Comment comment = commentRepository.findById(id).orElseThrow(() -> new RuntimeException("Comment not found"));
        comment.setApproved(true);
        return commentRepository.save(comment);
    }

    public void deleteComment(Long id, Long authorId) {
        Comment comment = commentRepository.findById(id).orElseThrow(() -> new RuntimeException("Comment not found"));
        if (!comment.getAuthor().getId().equals(authorId)) {
            throw new RuntimeException("You can only delete your own comments");
        }
        commentRepository.delete(comment);
    }
}
