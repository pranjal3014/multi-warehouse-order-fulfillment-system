package com.fulfillment.graphql;

import java.util.List;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import com.fulfillment.dto.response.UserResponse;
import com.fulfillment.service.UserService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class UserQueryResolver {

    private final UserService userService;

    @QueryMapping
    public List<UserResponse> users() {
        return userService.getAllUsers();
    }

    @QueryMapping
    public UserResponse userById(@Argument Long userId) {
        return userService.getUserById(userId);
    }

    @QueryMapping
    public List<UserResponse> pendingWarehouseManagers() {
        return userService.getPendingWarehouseManagers();
    }
}