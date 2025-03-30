package com.leverx.ratingsystem.service;

import com.leverx.ratingsystem.dto.UserDTO;
import com.leverx.ratingsystem.entity.Comment;
import com.leverx.ratingsystem.entity.Role;
import com.leverx.ratingsystem.entity.User;
import com.leverx.ratingsystem.repository.CommentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RatingServiceTest {

    @InjectMocks
    private RatingService ratingService;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private UserService userService;

    private User seller;

    @BeforeEach
    void setUp() {
        seller = new User();
        seller.setId(1L);
        seller.setRole(Role.SELLER);
        seller.setApproved(true);
    }

    @Test
    void calculateSellerRating_shouldReturnDefaultRating_whenNoApprovedComments() {
        when(userService.findSeller(1L)).thenReturn(seller);
        when(commentRepository.findBySellerAndApproved(seller, true)).thenReturn(List.of());

        UserDTO result = ratingService.calculateSellerRating(1L);

        assertEquals(0.0, result.getRating());
    }

    @Test
    void calculateSellerRating_shouldCalculateAverageRating() {
        when(userService.findSeller(1L)).thenReturn(seller);

        Comment c1 = Comment.builder().seller(seller).rating(4).approved(true).build();
        Comment c2 = Comment.builder().seller(seller).rating(5).approved(true).build();
        Comment c3 = Comment.builder().seller(seller).rating(3).approved(true).build();

        when(commentRepository.findBySellerAndApproved(seller, true)).thenReturn(List.of(c1, c2, c3));

        UserDTO result = ratingService.calculateSellerRating(1L);

        assertEquals(4.0, result.getRating());
    }

    @Test
    void getTopSellers_shouldReturnSellersSortedByRating() {
        User seller1 = new User();
        seller1.setId(1L);
        User seller2 = new User();
        seller2.setId(2L);

        when(userService.findByRoleAndIsApproved(Role.SELLER, true))
                .thenReturn(List.of(seller1, seller2));

        when(userService.findSeller(1L)).thenReturn(seller1);
        when(userService.findSeller(2L)).thenReturn(seller2);

        when(commentRepository.findBySellerAndApproved(seller1, true))
                .thenReturn(List.of(Comment.builder().seller(seller1).rating(4).approved(true).build()));

        when(commentRepository.findBySellerAndApproved(seller2, true))
                .thenReturn(List.of(Comment.builder().seller(seller2).rating(5).approved(true).build()));

        List<UserDTO> result = ratingService.getTopSellers();

        assertEquals(2, result.size());
        assertTrue(result.get(0).getRating() >= result.get(1).getRating());
    }

    @Test
    void filterSellersByRating_shouldReturnSellersWithinRange() {
        User seller1 = new User();
        seller1.setId(1L);
        User seller2 = new User();
        seller2.setId(2L);

        when(userService.findByRoleAndIsApproved(Role.SELLER, true))
                .thenReturn(List.of(seller1, seller2));

        when(userService.findSeller(1L)).thenReturn(seller1);
        when(userService.findSeller(2L)).thenReturn(seller2);

        when(commentRepository.findBySellerAndApproved(seller1, true))
                .thenReturn(List.of(Comment.builder().seller(seller1).rating(4).approved(true).build()));

        when(commentRepository.findBySellerAndApproved(seller2, true))
                .thenReturn(List.of(Comment.builder().seller(seller2).rating(2).approved(true).build()));

        List<UserDTO> result = ratingService.filterSellersByRating(3.0, 5.0);

        assertEquals(1, result.size());
        assertTrue(result.get(0).getRating() >= 3.0 && result.get(0).getRating() <= 5.0);
    }
}
