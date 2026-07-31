package com.fulfillment.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.fulfillment.entity.Order;
import com.fulfillment.event.OrderPlacedEvent;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OrderEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishOrderPlacedEvent(Order order) {

        OrderPlacedEvent event = OrderPlacedEvent.builder()
                .eventType("ORDER_PLACED")
                .orderId(order.getOrderId())
                .userId(order.getUserId())
                .build();

        kafkaTemplate.send("order-created", event);
    }
}
