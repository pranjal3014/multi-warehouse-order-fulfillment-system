package com.fulfillment.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TokenValidationResponse {

	private Long userId;
	private String userEmail;
	private String role;
}
