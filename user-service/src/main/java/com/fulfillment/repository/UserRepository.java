package com.fulfillment.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fulfillment.entity.User;
import com.fulfillment.enums.UserStatus;

public interface UserRepository extends JpaRepository<User, Long>{
	Optional<User> findByUserEmail(String email);

    Optional<User> findByUserContact(String phone);

    boolean existsByUserEmail(String email);

    boolean existsByUserContact(String contact);

    List<User> findByUserStatus(UserStatus status);
}
