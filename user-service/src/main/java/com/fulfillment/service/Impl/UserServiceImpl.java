package com.fulfillment.service.Impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.fulfillment.dto.request.LoginRequest;
import com.fulfillment.dto.request.UserRegistrationRequest;
import com.fulfillment.dto.request.UserUpdateRequest;
import com.fulfillment.dto.request.WarehouseManagerApplicationRequest;
import com.fulfillment.dto.response.UserResponse;
import com.fulfillment.dto.response.LoginResponse;
import com.fulfillment.dto.response.TokenValidationResponse;
import com.fulfillment.entity.User;
import com.fulfillment.enums.Role;
import com.fulfillment.enums.UserStatus;
import com.fulfillment.exception.EmailAlreadyExistsException;
import com.fulfillment.exception.UserNotFoundException;
import com.fulfillment.mapper.UserMapper;
import com.fulfillment.repository.UserRepository;
import com.fulfillment.service.UserService;
import com.fulfillment.service.JwtService;

import io.jsonwebtoken.Claims;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;
	private final UserMapper mapper;
	private final PasswordEncoder passwordEncoder;
	private final JwtService jwtService;

	@Override
	public UserResponse registerCustomer(UserRegistrationRequest request) {

		if (userRepository.existsByUserEmail(request.getUserEmail())) {
			throw new EmailAlreadyExistsException("Email already exists");
		}

		User user = new User();

		user.setUserName(request.getUserName());
		user.setUserEmail(request.getUserEmail());
		user.setUserContact(request.getUserContact());
		user.setUserPassword(passwordEncoder.encode(request.getUserPassword()));
		user.setRole(Role.CUSTOMER);
		user.setUserStatus(UserStatus.ACTIVE);
		User savedUser = userRepository.save(user);

		return mapper.toResponse(savedUser);
	}

	@Override
	public UserResponse authenticateUser(LoginRequest request) {

		User user = userRepository.findByUserEmail(request.getUserEmail())
				.orElseThrow(() -> new RuntimeException("Invalid user credentials"));

		if (!passwordEncoder.matches(request.getUserPassword(), user.getUserPassword())) {
			throw new RuntimeException("Invalid user credentials");
		}

		if (user.getUserStatus() != UserStatus.ACTIVE) {
			throw new RuntimeException("User is not active");
		}

		return mapper.toResponse(user);
	}

	@Override
	public LoginResponse login(LoginRequest request) {

		UserResponse user = authenticateUser(request);

		return LoginResponse.builder().token(jwtService.generateToken(user.getUserId(), user.getUserEmail(),
				user.getRole().name())).userId(user.getUserId()).userName(user.getUserName())
				.userEmail(user.getUserEmail()).role(user.getRole().name()).build();
	}

	@Override
	public TokenValidationResponse validateToken(String token) {

		Claims claims = jwtService.validateToken(token);

		return TokenValidationResponse.builder().userId(claims.get("userId", Long.class))
				.userEmail(claims.getSubject()).role(claims.get("role", String.class)).build();
	}

	@Override
	public UserResponse applyWarehouseManager(WarehouseManagerApplicationRequest request) {

		if (userRepository.existsByUserEmail(request.getUserEmail())) {
			throw new EmailAlreadyExistsException("Email already exists");
		}

		User user = new User();

		user.setUserName(request.getUserName());
		user.setUserEmail(request.getUserEmail());
		user.setUserContact(request.getUserContact());
		user.setUserPassword(passwordEncoder.encode(request.getUserPassword()));

		user.setRole(Role.WAREHOUSE_MANAGER);
		user.setUserStatus(UserStatus.PENDING);

		User savedUser = userRepository.save(user);

		return mapper.toResponse(savedUser);
	}

	@Override
	public UserResponse getUserById(Long userId) {

		User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));

		return mapper.toResponse(user);
	}

	@Override
	public List<UserResponse> getAllUsers() {

		return userRepository.findAll().stream().map(mapper::toResponse).toList();
	}

	@Override
	public List<UserResponse> getPendingWarehouseManagers() {

		return userRepository.findByUserStatus(UserStatus.PENDING).stream().map(mapper::toResponse).toList();
	}

	@Override
	public UserResponse updateUser(Long userId, UserUpdateRequest request) {

		User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));

		user.setUserName(request.getUserName());
		user.setUserContact(request.getUserContact());

		User updatedUser = userRepository.save(user);

		return mapper.toResponse(updatedUser);
	}

	@Override
	public Boolean deleteUser(Long userId) {

		User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));

		userRepository.delete(user);

		return true;
	}

	@Override
	public UserResponse approveWarehouseManager(Long userId, Long warehouseId) {

		User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));

		user.setUserStatus(UserStatus.ACTIVE);
		user.setWarehouseId(warehouseId);

		User updatedUser = userRepository.save(user);

		return mapper.toResponse(updatedUser);
	}

	@Override
	public UserResponse rejectWarehouseManager(Long userId) {

		User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));

		user.setUserStatus(UserStatus.REJECTED);

		User updatedUser = userRepository.save(user);

		return mapper.toResponse(updatedUser);
	}
}
