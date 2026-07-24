package com.fulfillment.notification_service.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fulfillment.notification_service.dto.event.OrderPlacedEvent;
import com.fulfillment.notification_service.dto.event.PaymentRefundEvent;
import com.fulfillment.notification_service.dto.event.PaymentSuccessEvent;
import com.fulfillment.notification_service.dto.event.ShipmentCreatedEvent;
import com.fulfillment.notification_service.dto.event.ShipmentDeliveredEvent;
import com.fulfillment.notification_service.dto.event.ShipmentShippedEvent;
import com.fulfillment.notification_service.enums.NotificationEventType;
import com.fulfillment.notification_service.exception.NotificationProcessingException;
import com.fulfillment.notification_service.service.NotificationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationEventConsumer {

    private final ObjectMapper objectMapper;

    private final NotificationService notificationService;

    @KafkaListener(topics = "order-events", groupId = "notification-service")
    public void consumeOrderEvent(String event) {

        OrderPlacedEvent orderPlacedEvent = readEvent(event, OrderPlacedEvent.class);

        notificationService.createNotification(
                orderPlacedEvent.getOrderId(),
                orderPlacedEvent.getUserId(),
                NotificationEventType.ORDER_PLACED,
                "Your Order #" + orderPlacedEvent.getOrderId() + " has been placed successfully.");
    }

    @KafkaListener(topics = "payment-events", groupId = "notification-service")
    public void consumePaymentEvent(String event) {

        NotificationEventType eventType = readEventType(event);

        if (eventType == NotificationEventType.PAYMENT_SUCCESS) {
            PaymentSuccessEvent paymentSuccessEvent = readEvent(event, PaymentSuccessEvent.class);

            notificationService.createNotification(
                    paymentSuccessEvent.getOrderId(),
                    paymentSuccessEvent.getUserId(),
                    NotificationEventType.PAYMENT_SUCCESS,
                    "Payment of ₹" + paymentSuccessEvent.getAmount()
                            + " received for Order #" + paymentSuccessEvent.getOrderId());
            return;
        }

        if (eventType == NotificationEventType.PAYMENT_REFUND) {
            PaymentRefundEvent paymentRefundEvent = readEvent(event, PaymentRefundEvent.class);

            notificationService.createNotification(
                    paymentRefundEvent.getOrderId(),
                    paymentRefundEvent.getUserId(),
                    NotificationEventType.PAYMENT_REFUND,
                    "Refund of ₹" + paymentRefundEvent.getAmount()
                            + " processed for Order #" + paymentRefundEvent.getOrderId());
            return;
        }

        log.warn("Unsupported payment event type : {}", eventType);
    }

    @KafkaListener(topics = "shipment-events", groupId = "notification-service")
    public void consumeShipmentEvent(String event) {

        NotificationEventType eventType = readEventType(event);

        if (eventType == NotificationEventType.SHIPMENT_CREATED) {
            ShipmentCreatedEvent shipmentCreatedEvent = readEvent(event, ShipmentCreatedEvent.class);

            notificationService.createNotification(
                    shipmentCreatedEvent.getOrderId(),
                    shipmentCreatedEvent.getUserId(),
                    NotificationEventType.SHIPMENT_CREATED,
                    "Shipment created for Order #" + shipmentCreatedEvent.getOrderId()
                            + ". Tracking Number : " + shipmentCreatedEvent.getTrackingNumber());
            return;
        }

        if (eventType == NotificationEventType.SHIPMENT_SHIPPED) {
            ShipmentShippedEvent shipmentShippedEvent = readEvent(event, ShipmentShippedEvent.class);

            notificationService.createNotification(
                    shipmentShippedEvent.getOrderId(),
                    shipmentShippedEvent.getUserId(),
                    NotificationEventType.SHIPMENT_SHIPPED,
                    "Order #" + shipmentShippedEvent.getOrderId() + " has been shipped. Tracking Number : "
                            + shipmentShippedEvent.getTrackingNumber());
            return;
        }

        if (eventType == NotificationEventType.SHIPMENT_DELIVERED) {
            ShipmentDeliveredEvent shipmentDeliveredEvent = readEvent(event, ShipmentDeliveredEvent.class);

            notificationService.createNotification(
                    shipmentDeliveredEvent.getOrderId(),
                    shipmentDeliveredEvent.getUserId(),
                    NotificationEventType.SHIPMENT_DELIVERED,
                    "Your Order #" + shipmentDeliveredEvent.getOrderId() + " has been delivered.");
            return;
        }

        log.warn("Unsupported shipment event type : {}", eventType);
    }

    private NotificationEventType readEventType(String event) {

        try {
            JsonNode jsonNode = objectMapper.readTree(event);

            return NotificationEventType.valueOf(jsonNode.get("eventType").asText());
        } catch (JsonProcessingException | IllegalArgumentException ex) {
            throw new NotificationProcessingException("Unable to read notification event type.", ex);
        }
    }

    private <T> T readEvent(String event, Class<T> eventClass) {

        try {
            return objectMapper.readValue(event, eventClass);
        } catch (JsonProcessingException ex) {
            throw new NotificationProcessingException("Unable to deserialize notification event.", ex);
        }
    }

}
