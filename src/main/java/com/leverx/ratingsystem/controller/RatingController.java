package com.leverx.ratingsystem.controller;

import com.leverx.ratingsystem.dto.UserDTO;
import com.leverx.ratingsystem.service.RatingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ratings")
public class RatingController {

    private final RatingService ratingService;

    public RatingController(RatingService ratingService) {
        this.ratingService = ratingService;
    }

    @GetMapping("/seller/{sellerId}")
    public ResponseEntity<UserDTO> getSellerRating(@PathVariable Long sellerId) {
        return ResponseEntity.ok(ratingService.calculateSellerRating(sellerId));
    }

    @GetMapping("/top-sellers")
    public ResponseEntity<List<UserDTO>> getTopSellers() {
        return ResponseEntity.ok(ratingService.getTopSellers());
    }

    @GetMapping("/filter")
    public ResponseEntity<List<UserDTO>> filterSellersByRating(
            @RequestParam double minRating,
            @RequestParam double maxRating) {
        return ResponseEntity.ok(ratingService.filterSellersByRating(minRating, maxRating));
    }
}

