package com.fulfillment.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.fulfillment.entity.OrderStatus;
import com.fulfillment.entity.PaymentStatus;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderResponse {

    private Long orderId;

    private Long userId;

    private BigDecimal totalAmount;

    private OrderStatus orderStatus;

    private PaymentStatus paymentStatus;

    private List<OrderItemResponse> orderItems;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}