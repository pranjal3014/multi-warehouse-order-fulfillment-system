package com.fulfillment.dto.response;

import java.time.LocalDateTime;

import com.fulfillment.enums.ShipmentStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShipmentResponse {

    private Long shipmentId;

    private Long orderId;

    private Long userId;

    private Long warehouseId;

    private String trackingNumber;

    private ShipmentStatus shipmentStatus;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}
