package com.fulfillment.service;

import java.util.List;

import com.fulfillment.dto.request.UserRegistrationRequest;
import com.fulfillment.dto.request.LoginRequest;
import com.fulfillment.dto.request.UserUpdateRequest;
import com.fulfillment.dto.request.WarehouseManagerApplicationRequest;
import com.fulfillment.dto.response.UserResponse;

public interface UserService {

	UserResponse registerCustomer(UserRegistrationRequest request);

	UserResponse authenticateUser(LoginRequest request);

	UserResponse applyWarehouseManager(WarehouseManagerApplicationRequest request);

	UserResponse getUserById(Long userId);

	List<UserResponse> getAllUsers();

	List<UserResponse> getPendingWarehouseManagers();

	UserResponse updateUser(Long userId, UserUpdateRequest request);

	Boolean deleteUser(Long userId);

	UserResponse approveWarehouseManager(Long userId, Long warehouseId);

	UserResponse rejectWarehouseManager(Long userId);
}
