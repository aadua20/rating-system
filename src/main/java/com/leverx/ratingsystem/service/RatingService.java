package com.leverx.ratingsystem.service;

import com.leverx.ratingsystem.entity.Comment;
import com.leverx.ratingsystem.entity.User;
import com.leverx.ratingsystem.repository.CommentRepository;
import com.leverx.ratingsystem.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RatingService {

    private final CommentRepository commentRepository;

    private final UserRepository userRepository;

    public RatingService(CommentRepository commentRepository, UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
    }

    public double calculateSellerRating(Long sellerId) {
        User seller = userRepository.findById(sellerId)
                .orElseThrow(() -> new RuntimeException("Seller not found"));

        List<Comment> approvedComments = commentRepository.findBySellerAndApproved(seller, true);

        if (approvedComments.isEmpty()) {
            return 0.0;
        }

        double sum = approvedComments.stream().mapToDouble(Comment::getRating).sum();
        return sum / approvedComments.size();
    }
}
