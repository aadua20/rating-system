package com.leverx.ratingsystem.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leverx.ratingsystem.dto.AuthResponse;
import com.leverx.ratingsystem.dto.LoginRequest;
import com.leverx.ratingsystem.dto.PasswordResetRequest;
import com.leverx.ratingsystem.entity.User;
import com.leverx.ratingsystem.exception.UserAuthException;
import com.leverx.ratingsystem.security.JwtUtil;
import com.leverx.ratingsystem.util.EmailService;
import com.leverx.ratingsystem.util.RedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final UserService userService;
    private final EmailService emailService;
    private final RedisService redisService;
    private final ObjectMapper objectMapper;

    public AuthResponse login(LoginRequest loginRequest) {
        String email = loginRequest.getEmail();
        log.info("Login attempt for email: {}", email);

        if (redisService.isEmailUnconfirmed(email)) {
            throw new UserAuthException("Email is not confirmed. Please check your email.");
        }

        Optional<User> userOptional = userService.findByEmail(email);
        if (userOptional.isEmpty()) {
            throw new UserAuthException("User not found.");
        }

        User user = userOptional.get();
        authenticateUser(email, loginRequest.getPassword());

        String jwtToken = jwtUtil.generateToken(user);
        log.info("Login successful for email: {}", email);

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
            log.info("Authentication successful for email: {}", email);
        } catch (Exception e) {
            throw new UserAuthException("Invalid credentials");
        }
    }

    public String register(User newUser) {
        String email = newUser.getEmail();
        log.info("Registration attempt for email: {}", email);

        if (userService.findByEmail(email).isPresent()) {
            throw new UserAuthException("An account with this email already exists.");
        }

        String confirmationCode = UUID.randomUUID().toString();
        try {
            String userJson = objectMapper.writeValueAsString(newUser);
            redisService.storeUserPendingConfirmation(email, confirmationCode, userJson);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error processing user data for Redis storage", e);
        }

        String confirmationLink = "http://localhost:8080/rating-system/auth/confirm?email=" + email + "&code=" + confirmationCode;
        emailService.sendConfirmationEmail(email, confirmationLink);
        log.info("Confirmation email sent to: {}", email);

        return "Registration successful! Please check your email to confirm your account.";
    }

    public String confirmEmail(String email, String code) {
        log.info("Email confirmation attempt for: {}", email);

        String userJson = redisService.validateAndRetrievePendingUser(email, code);
        if (userJson == null) {
            throw new UserAuthException("Invalid or expired confirmation code.");
        }

        try {
            User confirmedUser = objectMapper.readValue(userJson, User.class);
            userService.save(confirmedUser);
            redisService.removeConfirmationCode(email);
            return "Email confirmed successfully! You can now log in.";
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error processing user data during confirmation", e);
        }
    }

    public String forgotPassword(String email) {
        log.info("Password reset request for email: {}", email);

        Optional<User> userOptional = userService.findByEmail(email);
        if (userOptional.isEmpty()) {
            throw new UserAuthException("User not found.");
        }

        String resetCode = UUID.randomUUID().toString().replace("-", "").substring(0, 6);
        redisService.storeResetCode(email, resetCode);

        emailService.sendPasswordResetEmail(email, resetCode);
        log.info("Password reset code sent to email: {}", email);

        return "Password reset code sent to your email.";
    }

    public boolean checkResetCode(String email, String code) {
        log.info("Checking password reset code for email: {}", email);
        boolean isValid = redisService.validateResetCode(email, code);
        if (!isValid) {
            log.warn("Invalid or expired reset code for email: {}", email);
        }
        return isValid;
    }

    public String resetPassword(PasswordResetRequest resetRequest) {
        String email = resetRequest.getEmail();
        String code = resetRequest.getCode();
        String newPassword = resetRequest.getNewPassword();

        log.info("Resetting password for email: {}", email);

        if (!redisService.validateResetCode(email, code)) {
            throw new UserAuthException("Invalid or expired reset code.");
        }

        Optional<User> userOptional = userService.findByEmail(email);
        if (userOptional.isEmpty()) {
            throw new UserAuthException("User not found.");
        }

        User user = userOptional.get();
        user.setPassword(passwordEncoder.encode(newPassword));
        userService.save(user);

        redisService.removeResetCode(email);

        log.info("Password reset successful for email: {}", email);
        return "Password has been successfully reset.";
    }
}
