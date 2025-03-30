package com.leverx.ratingsystem.controller;

import com.leverx.ratingsystem.entity.GameObject;
import com.leverx.ratingsystem.service.GameObjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/objects")
public class GameObjectController {

    private final GameObjectService gameObjectService;

    public GameObjectController(GameObjectService gameObjectService) {
        this.gameObjectService = gameObjectService;
    }

    @Operation(summary = "Create a new game object")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Game object created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PostMapping
    public ResponseEntity<GameObject> createGameObject(
            @RequestParam String title,
            @RequestParam String text,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(gameObjectService.createGameObject(title, text, userDetails));
    }

    @Operation(summary = "Get all game objects")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Game objects retrieved successfully")
    })
    @GetMapping
    public ResponseEntity<List<GameObject>> getAllGameObjects() {
        return ResponseEntity.ok(gameObjectService.getAllGameObjects());
    }

    @Operation(summary = "Get game object by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Game object found"),
            @ApiResponse(responseCode = "404", description = "Game object not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<GameObject> getGameObjectById(@PathVariable Long id) {
        Optional<GameObject> gameObject = gameObjectService.getGameObjectById(id);
        return gameObject.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Update a game object by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Game object updated successfully"),
            @ApiResponse(responseCode = "403", description = "User not authorized to update this object"),
            @ApiResponse(responseCode = "404", description = "Game object not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<GameObject> updateGameObject(
            @PathVariable Long id,
            @RequestParam String newTitle,
            @RequestParam String newText,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(gameObjectService.updateGameObject(id, newTitle, newText, userDetails.getUsername()));
    }

    @Operation(summary = "Delete a game object by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Game object deleted successfully"),
            @ApiResponse(responseCode = "403", description = "User not authorized to delete this object"),
            @ApiResponse(responseCode = "404", description = "Game object not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGameObject(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        gameObjectService.deleteGameObject(id, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }
}
