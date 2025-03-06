package com.leverx.ratingsystem.mapper;

import com.leverx.ratingsystem.dto.RegisterRequest;
import com.leverx.ratingsystem.entity.Role;
import com.leverx.ratingsystem.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserMapper {

    private final PasswordEncoder passwordEncoder;

    public User registerDTOToEntity(RegisterRequest registerRequest) {
        if (registerRequest == null) {
            return null;
        }
        return User.builder()
                .firstName(registerRequest.getFirstName())
                .lastName(registerRequest.getLastName())
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .role(Role.valueOf(registerRequest.getRole()))
                .createdAt(LocalDateTime.now())
                .build();
    }
}
