package com.fulfillment.resolver;

import java.util.List;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import com.fulfillment.dto.request.CreateShipmentRequest;
import com.fulfillment.dto.request.UpdateShipmentStatusRequest;
import com.fulfillment.dto.response.ShipmentResponse;
import com.fulfillment.service.ShipmentService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ShipmentResolver {

    private final ShipmentService shipmentService;

    @MutationMapping
    public ShipmentResponse createShipment(@Argument @Valid CreateShipmentRequest createShipmentRequest) {
        return shipmentService.createShipment(createShipmentRequest);
    }

    @MutationMapping
    public ShipmentResponse updateShipmentStatus(@Argument @Valid UpdateShipmentStatusRequest updateShipmentStatusRequest) {
        return shipmentService.updateShipmentStatus(updateShipmentStatusRequest);
    }

    @QueryMapping
    public ShipmentResponse shipmentById(@Argument Long shipmentId) {
        return shipmentService.shipmentById(shipmentId);
    }

    @QueryMapping
    public ShipmentResponse shipmentByTrackingNumber(@Argument String trackingNumber) {
        return shipmentService.shipmentByTrackingNumber(trackingNumber);
    }

    @QueryMapping
    public List<ShipmentResponse> shipmentsByOrderId(@Argument Long orderId) {
        return shipmentService.shipmentsByOrderId(orderId);
    }

    @QueryMapping
    public List<ShipmentResponse> shipments() {
        return shipmentService.shipments();
    }

}
