package com.leverx.ratingsystem.service;

import com.leverx.ratingsystem.entity.User;
import com.leverx.ratingsystem.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()
                && !"anonymousUser".equals(authentication.getPrincipal())) {

            String email = authentication.getName();
            Optional<User> user = findByEmail(email);
            return user.orElse(null);
        }
        return null;
    }
}
