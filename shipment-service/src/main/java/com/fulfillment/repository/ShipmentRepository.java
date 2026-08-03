package com.fulfillment.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fulfillment.entity.Shipment;

@Repository
public interface ShipmentRepository extends JpaRepository<Shipment, Long> {

    Optional<Shipment> findByOrderIdAndWarehouseId(Long orderId, Long warehouseId);

    Optional<Shipment> findByTrackingNumber(String trackingNumber);

    List<Shipment> findByOrderId(Long orderId);

}
