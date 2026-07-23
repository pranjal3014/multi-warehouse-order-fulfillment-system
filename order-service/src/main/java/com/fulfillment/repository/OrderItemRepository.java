package com.fulfillment.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fulfillment.entity.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

}