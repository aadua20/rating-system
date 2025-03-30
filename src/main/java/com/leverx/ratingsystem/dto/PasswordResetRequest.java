package com.leverx.ratingsystem.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PasswordResetRequest {

    private String email;
    private String code;
    private String newPassword;
}
