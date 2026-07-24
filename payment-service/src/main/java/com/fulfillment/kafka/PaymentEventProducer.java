package com.fulfillment.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.fulfillment.entity.Payment;
import com.fulfillment.event.PaymentRefundEvent;
import com.fulfillment.event.PaymentSuccessEvent;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PaymentEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishPaymentSuccessEvent(Payment payment) {

        PaymentSuccessEvent event = PaymentSuccessEvent.builder()
                .eventType("PAYMENT_SUCCESS")
                .orderId(payment.getOrderId())
                .userId(payment.getUserId())
                .amount(payment.getAmount())
                .build();

        kafkaTemplate.send("payment-events", event);
    }

    public void publishPaymentRefundEvent(Payment payment) {

        PaymentRefundEvent event = PaymentRefundEvent.builder()
                .eventType("PAYMENT_REFUND")
                .orderId(payment.getOrderId())
                .userId(payment.getUserId())
                .amount(payment.getAmount())
                .build();

        kafkaTemplate.send("payment-events", event);
    }
}
