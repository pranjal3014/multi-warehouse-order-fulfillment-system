package com.fulfillment.dto.response;

import java.time.LocalDateTime;

import com.fulfillment.enums.Role;
import com.fulfillment.enums.UserStatus;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserResponse {
	
	private Long userId;
	private String userName;
	private String userEmail;
	private String userContact;
	private Role role;
	private UserStatus userStatus;
	private Long warehouseId;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
}
