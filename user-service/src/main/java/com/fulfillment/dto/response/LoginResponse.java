package com.fulfillment.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResponse {

	private String token;
	private Long userId;
	private String userName;
	private String userEmail;
	private String role;
}
