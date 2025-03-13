package com.leverx.ratingsystem.controller;

import com.leverx.ratingsystem.service.RatingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ratings")
public class RatingController {

    private final RatingService ratingService;

    public RatingController(RatingService ratingService) {
        this.ratingService = ratingService;
    }

    @GetMapping("/seller/{sellerId}")
    public ResponseEntity<Double> getSellerRating(@PathVariable Long sellerId) {
        return ResponseEntity.ok(ratingService.calculateSellerRating(sellerId));
    }
}
