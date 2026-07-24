package com.fulfillment.service;

import java.util.List;

import com.fulfillment.dto.request.CreateShipmentRequest;
import com.fulfillment.dto.request.UpdateShipmentStatusRequest;
import com.fulfillment.dto.response.ShipmentResponse;

public interface ShipmentService {

    ShipmentResponse createShipment(CreateShipmentRequest createShipmentRequest);

    ShipmentResponse updateShipmentStatus(UpdateShipmentStatusRequest updateShipmentStatusRequest);

    ShipmentResponse shipmentById(Long shipmentId);

    ShipmentResponse shipmentByTrackingNumber(String trackingNumber);

    List<ShipmentResponse> shipmentsByOrderId(Long orderId);

    List<ShipmentResponse> shipments();

}
