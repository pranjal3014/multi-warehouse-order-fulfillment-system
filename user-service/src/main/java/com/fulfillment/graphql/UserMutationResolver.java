package com.fulfillment.graphql;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

import com.fulfillment.dto.request.UserRegistrationRequest;
import com.fulfillment.dto.request.LoginRequest;
import com.fulfillment.dto.request.UserUpdateRequest;
import com.fulfillment.dto.request.WarehouseManagerApplicationRequest;
import com.fulfillment.dto.response.UserResponse;
import com.fulfillment.dto.response.LoginResponse;
import com.fulfillment.service.UserService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class UserMutationResolver {

	private final UserService userService;

	@MutationMapping
	public UserResponse registerCustomer(@Argument UserRegistrationRequest user) {

		return userService.registerCustomer(user);
	}

	@MutationMapping
	public UserResponse authenticateUser(@Argument LoginRequest request) {

		return userService.authenticateUser(request);
	}

	@MutationMapping
	public LoginResponse login(@Argument LoginRequest request) {

		return userService.login(request);
	}

	@MutationMapping
	public UserResponse applyWarehouseManager(@Argument WarehouseManagerApplicationRequest user) {

		return userService.applyWarehouseManager(user);
	}

	@MutationMapping
	public UserResponse updateUser(@Argument Long userId, @Argument UserUpdateRequest user) {

		return userService.updateUser(userId, user);
	}

	@MutationMapping
	public Boolean deleteUser(@Argument Long userId) {

		return userService.deleteUser(userId);
	}

	@MutationMapping
	public UserResponse approveWarehouseManager(@Argument Long userId, @Argument Long warehouseId) {

		return userService.approveWarehouseManager(userId, warehouseId);
	}

	@MutationMapping
	public UserResponse rejectWarehouseManager(@Argument Long userId) {

		return userService.rejectWarehouseManager(userId);
	}
}
