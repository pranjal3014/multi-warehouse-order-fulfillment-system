package com.fulfillment.dto.request;

import com.fulfillment.enums.ShipmentStatus;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateShipmentStatusRequest {

    @NotNull(message = "Shipment Id is required")
    private Long shipmentId;

    @NotNull(message = "Shipment Status is required")
    private ShipmentStatus shipmentStatus;

}
