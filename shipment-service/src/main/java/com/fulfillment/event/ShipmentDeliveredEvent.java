package com.fulfillment.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShipmentDeliveredEvent {

    private String eventType;

    private Long orderId;

    private Long userId;

    private String trackingNumber;

}
