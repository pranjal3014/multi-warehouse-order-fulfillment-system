package com.fulfillment.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import com.fulfillment.dto.response.OrderItemResponse;
import com.fulfillment.dto.response.OrderResponse;
import com.fulfillment.entity.Order;
import com.fulfillment.entity.OrderItem;

@Component
public class OrderMapper {

    public OrderResponse toOrderResponse(Order order) {

        return OrderResponse.builder()
                .orderId(order.getOrderId())
                .userId(order.getUserId())
                .totalAmount(order.getTotalAmount())
                .orderStatus(order.getOrderStatus())
                .paymentStatus(order.getPaymentStatus())
                .orderItems(toOrderItemResponseList(order.getOrderItems()))
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }

    public OrderItemResponse toOrderItemResponse(OrderItem orderItem) {

        return OrderItemResponse.builder()
                .orderItemId(orderItem.getOrderItemId())
                .productId(orderItem.getProductId())
                .warehouseId(orderItem.getWarehouseId())
                .quantity(orderItem.getQuantity())
                .price(orderItem.getPrice())
                .build();
    }

    public List<OrderItemResponse> toOrderItemResponseList(List<OrderItem> orderItems) {

        return orderItems.stream()
                .map(this::toOrderItemResponse)
                .toList();
    }

}