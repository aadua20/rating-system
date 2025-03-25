package com.leverx.ratingsystem.controller;

import com.leverx.ratingsystem.dto.AuthResponse;
import com.leverx.ratingsystem.dto.LoginRequest;
import com.leverx.ratingsystem.dto.PasswordResetRequest;
import com.leverx.ratingsystem.dto.RegisterRequest;
import com.leverx.ratingsystem.mapper.UserMapper;
import com.leverx.ratingsystem.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
@Slf4j
public class AuthController {

    private final AuthService authService;
    private final UserMapper userMapper;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> userSignIn(@Valid @RequestBody final LoginRequest loginRequest) {
        log.info("Login attempt for email: {}", loginRequest.getEmail());
        return ResponseEntity.ok(authService.login(loginRequest));
    }

    @PostMapping("/register")
    public ResponseEntity<String> userSignUp(@Valid @RequestBody final RegisterRequest registerRequest) {
        log.info("User registration attempt: {}", registerRequest.getEmail());
        String responseMessage = authService.register(userMapper.registerDTOToEntity(registerRequest));
        return ResponseEntity.ok(responseMessage);
    }

    @GetMapping("/confirm")
    public ResponseEntity<String> confirmEmail(
            @RequestParam("email") String email,
            @RequestParam("code") String code) {
        log.info("Confirming email: {}", email);
        String confirmationResponse = authService.confirmEmail(email, code);
        return ResponseEntity.ok(confirmationResponse);
    }

    @PostMapping("/forgot_password")
    public ResponseEntity<String> forgotPassword(@RequestParam("email") String email) {
        log.info("Password reset request for email: {}", email);
        return ResponseEntity.ok(authService.forgotPassword(email));
    }

    @GetMapping("/check_code")
    public ResponseEntity<String> checkResetCode(
            @RequestParam("email") String email,
            @RequestParam("code") String code) {
        log.info("Checking reset code for email: {}", email);
        boolean isValid = authService.checkResetCode(email, code);

        if (isValid) {
            return ResponseEntity.ok("The reset code is valid.");
        } else {
            return ResponseEntity.badRequest().body("Invalid or expired reset code.");
        }
    }

    @PostMapping("/reset")
    public ResponseEntity<String> resetPassword(@Valid @RequestBody PasswordResetRequest resetRequest) {
        log.info("Resetting password for email: {}", resetRequest.getEmail());
        return ResponseEntity.ok(authService.resetPassword(resetRequest));
    }
}
