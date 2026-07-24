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
public class OrderPlacedEvent {

    @Builder.Default
    private NotificationEventType eventType = NotificationEventType.ORDER_PLACED;

    private Long orderId;

    private Long userId;

}
