package com.fulfillment.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateShipmentRequest {

    @NotNull(message = "Order Id is required")
    private Long orderId;

    @NotNull(message = "User Id is required")
    private Long userId;

    @NotNull(message = "Warehouse Id is required")
    private Long warehouseId;

}
