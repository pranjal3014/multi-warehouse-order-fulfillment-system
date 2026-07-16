package com.fulfillment.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fulfillment.entity.Inventory;

public interface InventoryRepository extends JpaRepository<Inventory, Long>{
	Optional<Inventory> findByProductIdAndWarehouseWId(Long productId, Long warehouseId);
	List<Inventory> findByProductId(Long ProductId);
}
