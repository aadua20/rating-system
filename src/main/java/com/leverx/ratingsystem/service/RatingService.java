package com.leverx.ratingsystem.service;

import com.leverx.ratingsystem.dto.UserDTO;
import com.leverx.ratingsystem.entity.Comment;
import com.leverx.ratingsystem.entity.Role;
import com.leverx.ratingsystem.entity.User;
import com.leverx.ratingsystem.mapper.UserMapper;
import com.leverx.ratingsystem.repository.CommentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class RatingService {

    private final CommentRepository commentRepository;
    private final UserService userService;

    public RatingService(CommentRepository commentRepository, UserService userService) {
        this.commentRepository = commentRepository;
        this.userService = userService;
    }

    public UserDTO calculateSellerRating(Long sellerId) {
        User seller = userService.findSeller(sellerId);
        List<Comment> approvedComments = commentRepository.findBySellerAndApproved(seller, true);
        UserDTO userDTO = UserMapper.userToUserDTO(seller);

        if (approvedComments.isEmpty()) {
            log.info("No approved comments found for seller ID {}. Rating is default.", sellerId);
            return userDTO;
        }

        double sum = approvedComments.stream().mapToDouble(Comment::getRating).sum();
        double rating = BigDecimal.valueOf(sum)
                .divide(BigDecimal.valueOf(approvedComments.size()), 2, RoundingMode.HALF_UP)
                .doubleValue();

        userDTO.setRating(rating);
        log.info("Calculated rating {} for seller ID {}", rating, sellerId);

        return userDTO;
    }

    public List<UserDTO> getTopSellers() {
        log.info("Retrieving top sellers by rating");
        return userService.findByRoleAndIsApproved(Role.SELLER, true).stream()
                .map(user -> calculateSellerRating(user.getId()))
                .sorted(Comparator.comparingDouble(UserDTO::getRating).reversed())
                .collect(Collectors.toList());
    }

    public List<UserDTO> filterSellersByRating(double minRating, double maxRating) {
        log.info("Filtering sellers with rating between {} and {}", minRating, maxRating);
        return userService.findByRoleAndIsApproved(Role.SELLER, true).stream()
                .map(user -> calculateSellerRating(user.getId()))
                .filter(seller -> seller.getRating() >= minRating && seller.getRating() <= maxRating)
                .collect(Collectors.toList());
    }

}
