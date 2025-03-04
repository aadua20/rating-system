package com.leverx.ratingsystem.service;

import com.leverx.ratingsystem.dto.AuthResponse;
import com.leverx.ratingsystem.dto.LoginRequest;
import com.leverx.ratingsystem.entity.User;
import com.leverx.ratingsystem.exception.UserAuthException;
import com.leverx.ratingsystem.repository.UserRepository;
import com.leverx.ratingsystem.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;

    private final JwtUtil jwtUtil;

    private final AuthenticationManager authenticationManager;

    public AuthResponse login(LoginRequest loginRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()
                    )
            );
        } catch (Exception e) {
            throw new UserAuthException("Invalid credentials");
        }
        User user = userRepository.
                findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new UserAuthException(
                        "User with username {} does not exist", loginRequest.getEmail()
                ));
        String jwt = jwtUtil.generateToken(user);
        return AuthResponse.builder()
                .email(loginRequest.getEmail())
                .token(jwt)
                .build();
    }

    public AuthResponse register(User user) {
        Optional<User> userDB = userRepository.findByEmail(user.getEmail());
        if (userDB.isPresent()) {
            throw new UserAuthException("Account with email {} already exists.", user.getEmail());
        }
        Optional<User> userDB1 = userRepository.findByEmail(user.getUsername());
        if (userDB1.isPresent()) {
            throw new UserAuthException("Account with username {} already exists.", user.getUsername());
        }
        userRepository.save(user);
        String jwt = jwtUtil.generateToken(user);
        return AuthResponse.builder()
                .token(jwt)
                .build();
    }
}
