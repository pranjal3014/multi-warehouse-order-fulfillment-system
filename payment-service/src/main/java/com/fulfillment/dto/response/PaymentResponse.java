package com.fulfillment.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fulfillment.enums.PaymentMethod;
import com.fulfillment.enums.PaymentStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentResponse {

    private Long paymentId;

    private Long orderId;

    private Long userId;

    private BigDecimal amount;

    private PaymentMethod paymentMethod;

    private PaymentStatus paymentStatus;

    private String transactionId;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}