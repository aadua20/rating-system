package com.leverx.ratingsystem.controller;

import com.leverx.ratingsystem.dto.AuthResponse;
import com.leverx.ratingsystem.dto.LoginRequest;
import com.leverx.ratingsystem.dto.PasswordResetRequest;
import com.leverx.ratingsystem.dto.RegisterRequest;
import com.leverx.ratingsystem.mapper.UserMapper;
import com.leverx.ratingsystem.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
@Slf4j
public class AuthController {

    private final AuthService authService;
    private final UserMapper userMapper;

    @Operation(summary = "Log in a user and return a JWT token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successful"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> userSignIn(@Valid @RequestBody final LoginRequest loginRequest) {
        log.info("Login attempt for email: {}", loginRequest.getEmail());
        return ResponseEntity.ok(authService.login(loginRequest));
    }

    @Operation(summary = "Register a new user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Registration successful"),
            @ApiResponse(responseCode = "400", description = "Invalid registration data")
    })
    @PostMapping("/register")
    public ResponseEntity<String> userSignUp(@Valid @RequestBody final RegisterRequest registerRequest) {
        log.info("User registration attempt: {}", registerRequest.getEmail());
        String responseMessage = authService.register(userMapper.registerDTOToEntity(registerRequest));
        return ResponseEntity.ok(responseMessage);
    }

    @Operation(summary = "Confirm user's email using a code")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Email confirmed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid or expired confirmation code")
    })
    @GetMapping("/confirm")
    public ResponseEntity<String> confirmEmail(
            @RequestParam("email") String email,
            @RequestParam("code") String code) {
        log.info("Confirming email: {}", email);
        String confirmationResponse = authService.confirmEmail(email, code);
        return ResponseEntity.ok(confirmationResponse);
    }

    @Operation(summary = "Request a password reset link")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password reset email sent")
    })
    @PostMapping("/forgot_password")
    public ResponseEntity<String> forgotPassword(@RequestParam("email") String email) {
        log.info("Password reset request for email: {}", email);
        return ResponseEntity.ok(authService.forgotPassword(email));
    }

    @Operation(summary = "Check the validity of a password reset code")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reset code is valid"),
            @ApiResponse(responseCode = "400", description = "Invalid or expired reset code")
    })
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

    @Operation(summary = "Reset password using a valid reset code")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password reset successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid reset code or request data")
    })
    @PostMapping("/reset")
    public ResponseEntity<String> resetPassword(@Valid @RequestBody PasswordResetRequest resetRequest) {
        log.info("Resetting password for email: {}", resetRequest.getEmail());
        return ResponseEntity.ok(authService.resetPassword(resetRequest));
    }
}
