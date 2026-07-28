package com.fulfillment.dto;

import lombok.Data;

@Data
public class LoginRequest {

    private String userEmail;
    private String userPassword;
    
}
