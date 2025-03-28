package com.leverx.ratingsystem.controller;

import com.leverx.ratingsystem.entity.GameObject;
import com.leverx.ratingsystem.service.GameObjectService;
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

    @PostMapping
    public ResponseEntity<GameObject> createGameObject(
            @RequestParam String title,
            @RequestParam String text,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(gameObjectService.createGameObject(title, text, userDetails));
    }

    @GetMapping
    public ResponseEntity<List<GameObject>> getAllGameObjects() {
        return ResponseEntity.ok(gameObjectService.getAllGameObjects());
    }

    @GetMapping("/{id}")
    public ResponseEntity<GameObject> getGameObjectById(@PathVariable Long id) {
        Optional<GameObject> gameObject = gameObjectService.getGameObjectById(id);
        return gameObject.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<GameObject> updateGameObject(
            @PathVariable Long id,
            @RequestParam String newTitle,
            @RequestParam String newText,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(gameObjectService.updateGameObject(id, newTitle, newText, userDetails.getUsername()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGameObject(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        gameObjectService.deleteGameObject(id, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }
}
