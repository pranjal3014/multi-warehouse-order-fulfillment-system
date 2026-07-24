package com.fulfillment.notification_service.dto.event;

import com.fulfillment.notification_service.enums.NotificationEventType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShipmentCreatedEvent {

    @Builder.Default
    private NotificationEventType eventType = NotificationEventType.SHIPMENT_CREATED;

    private Long orderId;

    private Long userId;

    private String trackingNumber;

}
