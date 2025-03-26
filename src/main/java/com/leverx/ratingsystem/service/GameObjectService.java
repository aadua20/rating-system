package com.leverx.ratingsystem.service;

import com.leverx.ratingsystem.entity.GameObject;
import com.leverx.ratingsystem.entity.User;
import com.leverx.ratingsystem.repository.GameObjectRepository;
import com.leverx.ratingsystem.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class GameObjectService {

    private final GameObjectRepository gameObjectRepository;
    private final UserRepository userRepository;

    public GameObjectService(GameObjectRepository gameObjectRepository, UserRepository userRepository) {
        this.gameObjectRepository = gameObjectRepository;
        this.userRepository = userRepository;
    }

    public GameObject createGameObject(String title, String text, String username) {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("User not authorized"));

        GameObject gameObject = GameObject.builder()
                .title(title)
                .text(text)
                .user(user)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        return gameObjectRepository.save(gameObject);
    }

    public List<GameObject> getAllGameObjects() {
        return gameObjectRepository.findAll();
    }

    public Optional<GameObject> getGameObjectById(Long id) {
        return gameObjectRepository.findById(id);
    }

    public GameObject updateGameObject(Long id, String newTitle, String newText, String username) {
        GameObject gameObject = gameObjectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("GameObject not found"));

        if (!gameObject.getUser().getEmail().equals(username)) {
            throw new RuntimeException("You can only update your own game objects");
        }

        gameObject.setTitle(newTitle);
        gameObject.setText(newText);
        gameObject.setUpdatedAt(LocalDateTime.now());

        return gameObjectRepository.save(gameObject);
    }

    public void deleteGameObject(Long id, String username) {
        GameObject gameObject = gameObjectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("GameObject not found"));

        if (!gameObject.getUser().getEmail().equals(username)) {
            throw new RuntimeException("You can only delete your own game objects");
        }

        gameObjectRepository.delete(gameObject);
    }
}
