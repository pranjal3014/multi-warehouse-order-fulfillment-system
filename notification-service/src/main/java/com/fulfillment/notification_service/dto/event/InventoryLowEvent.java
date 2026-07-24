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
public class InventoryLowEvent {

    @Builder.Default
    private NotificationEventType eventType = NotificationEventType.INVENTORY_LOW;

    private Long productId;

    private Long warehouseId;

    private Integer availableQuantity;

}
