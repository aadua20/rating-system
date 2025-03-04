package com.leverx.ratingsystem.controller;

import com.leverx.ratingsystem.dto.AuthResponse;
import com.leverx.ratingsystem.dto.LoginRequest;
import com.leverx.ratingsystem.dto.RegisterRequest;
import com.leverx.ratingsystem.mapper.UserMapper;
import com.leverx.ratingsystem.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
@Slf4j
public class AuthController {

    private final AuthService authService;

    private final UserMapper userMapper;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> userSignIn(@Valid @RequestBody final LoginRequest loginRequest) {
        log.info("Logging in...");
        return ResponseEntity.ok(authService.login(loginRequest));
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> userSignUp(@Valid @RequestBody final RegisterRequest registerRequest) {
        log.info("Signing up...");
        AuthResponse authResponse = authService.register(userMapper.registerDTOToEntity(registerRequest));
        return ResponseEntity.ok(authResponse);
    }
}
