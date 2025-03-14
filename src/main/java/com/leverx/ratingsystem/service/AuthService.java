package com.leverx.ratingsystem.service;

import com.leverx.ratingsystem.dto.AuthResponse;
import com.leverx.ratingsystem.dto.LoginRequest;
import com.leverx.ratingsystem.entity.User;
import com.leverx.ratingsystem.exception.UserAuthException;
import com.leverx.ratingsystem.repository.UserRepository;
import com.leverx.ratingsystem.security.JwtUtil;
import com.leverx.ratingsystem.util.EmailService;
import com.leverx.ratingsystem.util.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;
    private final RedisService redisService;

    public AuthResponse login(LoginRequest loginRequest) {
        Optional<User> userOptional = userRepository.findByEmail(loginRequest.getEmail());

        if (userOptional.isEmpty()) {
            throw new UserAuthException("User not found.");
        }

        User user = userOptional.get();

        // Check if email is confirmed
        if (redisService.isEmailUnconfirmed(user.getEmail())) {
            throw new UserAuthException("Email is not confirmed. Please check your email.");
        }

        // Authenticate user
        Authentication authentication = authenticateUser(loginRequest.getEmail(), loginRequest.getPassword());

        // Generate JWT token
        String jwtToken = jwtUtil.generateToken(user);

        return AuthResponse.builder()
                .email(user.getEmail())
                .token(jwtToken)
                .build();
    }

    private Authentication authenticateUser(String email, String password) {
        try {
            return authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
            );
        } catch (Exception e) {
            throw new UserAuthException("Invalid credentials");
        }
    }

    public String register(User newUser) {
        Optional<User> existingUser = userRepository.findByEmail(newUser.getEmail());
        if (existingUser.isPresent()) {
            throw new UserAuthException("Account with email " + newUser.getEmail() + " already exists.");
        }

        // Save user to the database
        userRepository.save(newUser);

        // Generate confirmation code and store in Redis
        String confirmationCode = UUID.randomUUID().toString();
        redisService.storeConfirmationCode(newUser.getEmail(), confirmationCode);

        // Send confirmation email
        String confirmationLink = "http://localhost:8080/rating-system/auth/confirm?email=" + newUser.getEmail() + "&code=" + confirmationCode;
        emailService.sendConfirmationEmail(newUser.getEmail(), confirmationLink);

        return "Registration successful. Check your email to confirm your account.";
    }

    public String confirmEmail(String email, String code) {
        if (redisService.validateConfirmationCode(email, code)) {
            redisService.removeConfirmationCode(email);
            return "Email confirmed successfully. You can now log in.";
        } else {
            throw new UserAuthException("Invalid or expired confirmation code.");
        }
    }
}
