package com.fulfillment.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.fulfillment.entity.Shipment;
import com.fulfillment.event.ShipmentCreatedEvent;
import com.fulfillment.event.ShipmentDeliveredEvent;
import com.fulfillment.event.ShipmentShippedEvent;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ShipmentEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishShipmentCreatedEvent(Shipment shipment) {

        ShipmentCreatedEvent event = ShipmentCreatedEvent.builder()
                .eventType("SHIPMENT_CREATED")
                .orderId(shipment.getOrderId())
                .userId(shipment.getUserId())
                .trackingNumber(shipment.getTrackingNumber())
                .build();

        kafkaTemplate.send("shipment-events", event);
    }

    public void publishShipmentShippedEvent(Shipment shipment) {

        ShipmentShippedEvent event = ShipmentShippedEvent.builder()
                .eventType("SHIPMENT_SHIPPED")
                .orderId(shipment.getOrderId())
                .userId(shipment.getUserId())
                .trackingNumber(shipment.getTrackingNumber())
                .build();

        kafkaTemplate.send("shipment-events", event);
    }

    public void publishShipmentDeliveredEvent(Shipment shipment) {

        ShipmentDeliveredEvent event = ShipmentDeliveredEvent.builder()
                .eventType("SHIPMENT_DELIVERED")
                .orderId(shipment.getOrderId())
                .userId(shipment.getUserId())
                .trackingNumber(shipment.getTrackingNumber())
                .build();

        kafkaTemplate.send("shipment-events", event);
    }
}
