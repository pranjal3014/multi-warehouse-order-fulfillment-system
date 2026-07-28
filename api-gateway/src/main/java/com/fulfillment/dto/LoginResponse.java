package com.fulfillment.dto;

import lombok.Builder;
import lombok.Data;
import lombok.AllArgsConstructor;

@Data
@Builder
@AllArgsConstructor
public class LoginResponse {

    private String token;

    private Long userId;

    private String userName;

    private String userEmail;

    private String role;
}
