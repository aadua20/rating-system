package com.leverx.ratingsystem.dto;

import lombok.Data;

@Data
public class PasswordResetRequest {
    private String email;
    private String code;
    private String newPassword;
}
