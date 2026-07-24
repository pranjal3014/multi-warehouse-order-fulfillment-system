package com.fulfillment.service.Impl;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fulfillment.dto.request.CreateShipmentRequest;
import com.fulfillment.dto.request.UpdateShipmentStatusRequest;
import com.fulfillment.dto.response.ShipmentResponse;
import com.fulfillment.entity.Shipment;
import com.fulfillment.enums.ShipmentStatus;
import com.fulfillment.exception.ShipmentAlreadyExistsException;
import com.fulfillment.exception.ShipmentNotFoundException;
import com.fulfillment.mapper.ShipmentMapper;
import com.fulfillment.kafka.ShipmentEventProducer;
import com.fulfillment.repository.ShipmentRepository;
import com.fulfillment.service.ShipmentService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ShipmentServiceImpl implements ShipmentService {

    private final ShipmentRepository shipmentRepository;

    private final ShipmentMapper shipmentMapper;

    private final ShipmentEventProducer shipmentEventProducer;

    @Override
    public ShipmentResponse createShipment(CreateShipmentRequest createShipmentRequest) {

        shipmentRepository.findByOrderIdAndWarehouseId(
                createShipmentRequest.getOrderId(), createShipmentRequest.getWarehouseId())
                .ifPresent(shipment -> {
                    throw new ShipmentAlreadyExistsException(
                            "Shipment already exists for Order Id : " + createShipmentRequest.getOrderId()
                                    + " and Warehouse Id : " + createShipmentRequest.getWarehouseId());
                });

        Shipment shipment = shipmentMapper.toEntity(createShipmentRequest);

        shipment.setTrackingNumber("SHP-" + UUID.randomUUID());

        shipment.setShipmentStatus(ShipmentStatus.ALLOCATED);

        Shipment savedShipment = shipmentRepository.save(shipment);

        shipmentEventProducer.publishShipmentCreatedEvent(savedShipment);

        return shipmentMapper.toResponse(savedShipment);
    }

    @Override
    public ShipmentResponse updateShipmentStatus(UpdateShipmentStatusRequest updateShipmentStatusRequest) {

        Shipment shipment = shipmentRepository.findById(updateShipmentStatusRequest.getShipmentId())
                .orElseThrow(() -> new ShipmentNotFoundException(
                        "Shipment not found with Id : " + updateShipmentStatusRequest.getShipmentId()));

        validateShipmentStatus(shipment.getShipmentStatus(), updateShipmentStatusRequest.getShipmentStatus());

        shipment.setShipmentStatus(updateShipmentStatusRequest.getShipmentStatus());

        Shipment updatedShipment = shipmentRepository.save(shipment);

        if (updatedShipment.getShipmentStatus() == ShipmentStatus.SHIPPED) {
            shipmentEventProducer.publishShipmentShippedEvent(updatedShipment);
        }

        if (updatedShipment.getShipmentStatus() == ShipmentStatus.DELIVERED) {
            shipmentEventProducer.publishShipmentDeliveredEvent(updatedShipment);
        }

        return shipmentMapper.toResponse(updatedShipment);
    }

    @Override
    @Transactional(readOnly = true)
    public ShipmentResponse shipmentById(Long shipmentId) {

        Shipment shipment = shipmentRepository.findById(shipmentId)
                .orElseThrow(() -> new ShipmentNotFoundException(
                        "Shipment not found with Id : " + shipmentId));

        return shipmentMapper.toResponse(shipment);
    }

    @Override
    @Transactional(readOnly = true)
    public ShipmentResponse shipmentByTrackingNumber(String trackingNumber) {

        Shipment shipment = shipmentRepository.findByTrackingNumber(trackingNumber)
                .orElseThrow(() -> new ShipmentNotFoundException(
                        "Shipment not found with Tracking Number : " + trackingNumber));

        return shipmentMapper.toResponse(shipment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ShipmentResponse> shipmentsByOrderId(Long orderId) {

        return shipmentRepository.findByOrderId(orderId)
                .stream()
                .map(shipmentMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ShipmentResponse> shipments() {

        return shipmentRepository.findAll()
                .stream()
                .map(shipmentMapper::toResponse)
                .toList();
    }

    private void validateShipmentStatus(ShipmentStatus currentStatus, ShipmentStatus requestedStatus) {

        if (currentStatus == ShipmentStatus.ALLOCATED && requestedStatus == ShipmentStatus.SHIPPED) {
            return;
        }

        if (currentStatus == ShipmentStatus.SHIPPED && requestedStatus == ShipmentStatus.IN_TRANSIT) {
            return;
        }

        if (currentStatus == ShipmentStatus.IN_TRANSIT && requestedStatus == ShipmentStatus.DELIVERED) {
            return;
        }

        throw new IllegalStateException(
                "Shipment status cannot be changed from " + currentStatus + " to " + requestedStatus);
    }

}
