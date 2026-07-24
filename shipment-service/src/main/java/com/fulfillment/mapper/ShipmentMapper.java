package com.fulfillment.mapper;

import org.springframework.stereotype.Component;

import com.fulfillment.dto.request.CreateShipmentRequest;
import com.fulfillment.dto.response.ShipmentResponse;
import com.fulfillment.entity.Shipment;

@Component
public class ShipmentMapper {

    public Shipment toEntity(CreateShipmentRequest request) {

        return Shipment.builder()
                .orderId(request.getOrderId())
                .userId(request.getUserId())
                .warehouseId(request.getWarehouseId())
                .build();
    }

    public ShipmentResponse toResponse(Shipment shipment) {

        return ShipmentResponse.builder()
                .shipmentId(shipment.getShipmentId())
                .orderId(shipment.getOrderId())
                .userId(shipment.getUserId())
                .warehouseId(shipment.getWarehouseId())
                .trackingNumber(shipment.getTrackingNumber())
                .shipmentStatus(shipment.getShipmentStatus())
                .createdAt(shipment.getCreatedAt())
                .updatedAt(shipment.getUpdatedAt())
                .build();
    }

}
