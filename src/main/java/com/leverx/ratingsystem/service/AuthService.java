package com.leverx.ratingsystem.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leverx.ratingsystem.dto.AuthResponse;
import com.leverx.ratingsystem.dto.LoginRequest;
import com.leverx.ratingsystem.entity.User;
import com.leverx.ratingsystem.exception.UserAuthException;
import com.leverx.ratingsystem.repository.UserRepository;
import com.leverx.ratingsystem.security.JwtUtil;
import com.leverx.ratingsystem.util.EmailService;
import com.leverx.ratingsystem.util.RedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;
    private final RedisService redisService;
    private final ObjectMapper objectMapper;

    public AuthResponse login(LoginRequest loginRequest) {
        String email = loginRequest.getEmail();

        // Check if email is still unconfirmed in Redis
        if (redisService.isEmailUnconfirmed(email)) {
            throw new UserAuthException("Email is not confirmed. Please check your email.");
        }

        // Fetch user from the database only if email is confirmed
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isEmpty()) {
            throw new UserAuthException("User not found.");
        }

        User user = userOptional.get();

        // Authenticate user
        authenticateUser(email, loginRequest.getPassword());

        // Generate JWT token
        String jwtToken = jwtUtil.generateToken(user);

        return AuthResponse.builder()
                .email(user.getEmail())
                .token(jwtToken)
                .build();
    }

    private void authenticateUser(String email, String password) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
            );
        } catch (Exception e) {
            throw new UserAuthException("Invalid credentials");
        }
    }

    public String register(User newUser) {
        Optional<User> existingUser = userRepository.findByEmail(newUser.getEmail());
        if (existingUser.isPresent()) {
            throw new UserAuthException("An account with this email already exists.");
        }

        // Generate confirmation code and store user data in Redis
        String confirmationCode = UUID.randomUUID().toString();
        try {
            String userJson = objectMapper.writeValueAsString(newUser);
            redisService.storeUserPendingConfirmation(newUser.getEmail(), confirmationCode, userJson);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error processing user data for Redis storage", e);
        }

        // Send confirmation email
        String confirmationLink = "http://localhost:8080/auth/confirm?email=" + newUser.getEmail() + "&code=" + confirmationCode;
        emailService.sendConfirmationEmail(newUser.getEmail(), confirmationLink);

        return "Registration successful! Please check your email to confirm your account.";
    }

    public String confirmEmail(String email, String code) {
        // Validate confirmation code and retrieve user data from Redis
        String userJson = redisService.validateAndRetrievePendingUser(email, code);
        if (userJson == null) {
            throw new UserAuthException("Invalid or expired confirmation code.");
        }

        try {
            User confirmedUser = objectMapper.readValue(userJson, User.class);
            userRepository.save(confirmedUser);
            redisService.removeConfirmationCode(email);
            return "Email confirmed successfully! You can now log in.";
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error processing user data during confirmation", e);
        }
    }
}
