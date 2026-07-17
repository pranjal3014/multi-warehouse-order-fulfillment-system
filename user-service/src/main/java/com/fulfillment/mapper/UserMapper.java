package com.fulfillment.mapper;

import org.springframework.stereotype.Component;

import com.fulfillment.dto.response.UserResponse;
import com.fulfillment.entity.User;

@Component
public class UserMapper {

    public UserResponse toResponse(User user) {

        return UserResponse.builder()
                .userId(user.getUserId())
                .userName(user.getUserName())
                .userEmail(user.getUserEmail())
                .userContact(user.getUserContact())
                .role(user.getRole())
                .userStatus(user.getUserStatus())
                .warehouseId(user.getWarehouseId())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
