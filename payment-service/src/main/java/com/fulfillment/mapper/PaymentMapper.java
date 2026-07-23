package com.fulfillment.mapper;

import org.springframework.stereotype.Component;

import com.fulfillment.dto.request.PaymentRequest;
import com.fulfillment.dto.response.PaymentResponse;
import com.fulfillment.entity.Payment;

@Component
public class PaymentMapper {

    public Payment toEntity(PaymentRequest request) {

        return Payment.builder()
                .orderId(request.getOrderId())
                .userId(request.getUserId())
                .amount(request.getAmount())
                .paymentMethod(request.getPaymentMethod())
                .build();
    }

    public PaymentResponse toResponse(Payment payment) {

        return PaymentResponse.builder()
                .paymentId(payment.getPaymentId())
                .orderId(payment.getOrderId())
                .userId(payment.getUserId())
                .amount(payment.getAmount())
                .paymentMethod(payment.getPaymentMethod())
                .paymentStatus(payment.getPaymentStatus())
                .transactionId(payment.getTransactionId())
                .createdAt(payment.getCreatedAt())
                .updatedAt(payment.getUpdatedAt())
                .build();
    }

}