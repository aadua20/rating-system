package com.leverx.ratingsystem.mapper;

import com.leverx.ratingsystem.dto.RegisterRequest;
import com.leverx.ratingsystem.dto.UserDTO;
import com.leverx.ratingsystem.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserMapper {

    private final PasswordEncoder passwordEncoder;

    public static UserDTO userToUserDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .rating(0.0)
                .build();
    }

    public User registerDTOToEntity(RegisterRequest registerRequest) {
        if (registerRequest == null) {
            return null;
        }
        return User.builder()
                .firstName(registerRequest.getFirstName())
                .lastName(registerRequest.getLastName())
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .role(registerRequest.getRole())
                .createdAt(LocalDateTime.now())
                .isApproved(false)
                .build();
    }
}
