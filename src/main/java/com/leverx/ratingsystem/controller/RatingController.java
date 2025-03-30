package com.leverx.ratingsystem.controller;

import com.leverx.ratingsystem.dto.UserDTO;
import com.leverx.ratingsystem.service.RatingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @Operation(summary = "Get seller's average rating by seller ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Seller rating retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Seller not found")
    })
    @GetMapping("/seller/{sellerId}")
    public ResponseEntity<UserDTO> getSellerRating(@PathVariable Long sellerId) {
        return ResponseEntity.ok(ratingService.calculateSellerRating(sellerId));
    }

    @Operation(summary = "Get top-rated sellers")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Top sellers retrieved successfully")
    })
    @GetMapping("/top-sellers")
    public ResponseEntity<List<UserDTO>> getTopSellers() {
        return ResponseEntity.ok(ratingService.getTopSellers());
    }

    @Operation(summary = "Filter sellers by minimum and maximum rating")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sellers filtered successfully")
    })
    @GetMapping("/filter")
    public ResponseEntity<List<UserDTO>> filterSellersByRating(
            @RequestParam double minRating,
            @RequestParam double maxRating) {
        return ResponseEntity.ok(ratingService.filterSellersByRating(minRating, maxRating));
    }
}
