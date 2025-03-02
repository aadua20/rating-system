package com.leverx.ratingsystem.service;

import com.leverx.ratingsystem.entity.User;
import com.leverx.ratingsystem.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<User> findById(Long userId) {
        return userRepository.findById(userId);
    }
}
