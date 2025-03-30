package com.leverx.ratingsystem.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ValidationErrorResponse {

    private int status;
    private String error;
    private List<String> details;
}
