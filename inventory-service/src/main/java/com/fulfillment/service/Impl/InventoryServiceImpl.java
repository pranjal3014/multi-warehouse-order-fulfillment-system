package com.fulfillment.service.Impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;

import com.fulfillment.dto.request.InventoryRequest;
import com.fulfillment.dto.response.InventoryResponse;
import com.fulfillment.entity.Inventory;
import com.fulfillment.entity.Warehouse;
import com.fulfillment.exception.InventoryNotFoundException;
import com.fulfillment.exception.WarehouseNotFoundException;
import com.fulfillment.mapper.InventoryMapper;
import com.fulfillment.repository.InventoryRepository;
import com.fulfillment.repository.WarehouseRepository;
import com.fulfillment.service.InventoryService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

	private final InventoryRepository inventoryRepository;
	private final WarehouseRepository warehouseRepository;
	private final InventoryMapper mapper;

	@Override
	@CacheEvict(value = { "inventory", "inventoryByProduct" }, allEntries = true)
	public InventoryResponse createInventory(InventoryRequest request) {
		Warehouse warehouse = warehouseRepository.findById(request.getWarehouseId())
				.orElseThrow(() -> new WarehouseNotFoundException("Warehouse Not Found"));

		Inventory inventory = Inventory.builder().productId(request.getProductId()).warehouse(warehouse)
				.availableQty(request.getAvailableQyt()).reservedQty(0).build();

		Inventory savedInventory = inventoryRepository.save(inventory);

		return mapper.toInventoryResponse(savedInventory);
	}

	@Override
	@Cacheable(value = "inventory", key = "#inventoryId")
	public InventoryResponse getInventoryById(Long inventoryId) {
		Inventory inventory = inventoryRepository.findById(inventoryId)
				.orElseThrow(() -> new InventoryNotFoundException("Inventory Not Found"));

		return mapper.toInventoryResponse(inventory);
	}

	@Override
	@Transactional(readOnly = true)
	@Cacheable(value = "inventoryByProduct", key = "#productId")
	public List<InventoryResponse> getInventoryByProduct(Long productId) {
		return inventoryRepository.findByProductId(productId).stream().map(mapper::toInventoryResponse).toList();
	}

	@Override
	@CacheEvict(value = { "inventory", "inventoryByProduct" }, allEntries = true)
	public InventoryResponse updateInventory(Long inventoryId, InventoryRequest request) {
		Inventory inventory = inventoryRepository.findById(inventoryId)
				.orElseThrow(() -> new RuntimeException("Inventory Not Found"));

		inventory.setAvailableQty(request.getAvailableQyt());

		Inventory updatedInventory = inventoryRepository.save(inventory);

		return mapper.toInventoryResponse(updatedInventory);
	}

	@Override
	@CacheEvict(value = { "inventory", "inventoryByProduct" }, allEntries = true)
	public Boolean deleteInventory(Long inventoryId) {
		inventoryRepository.deleteById(inventoryId);

		return true;
	}

	@Override
	@Transactional
	public void reserveInventory(Long productId, Long warehouseId, Integer quantity) {

		Inventory inventory = inventoryRepository.findByProductIdAndWarehouseWId(productId, warehouseId)
				.orElseThrow(() -> new InventoryNotFoundException("Inventory Not Found"));

		if (inventory.getAvailableQty() < quantity) {
			throw new IllegalStateException("Insufficient inventory available.");
		}

		inventory.setAvailableQty(inventory.getAvailableQty() - quantity);

		inventory.setReservedQty(inventory.getReservedQty() + quantity);

		inventoryRepository.save(inventory);
	}

	@Override
	@Transactional
	public void releaseInventory(Long productId, Long warehouseId, Integer quantity) {

		Inventory inventory = inventoryRepository.findByProductIdAndWarehouseWId(productId, warehouseId)
				.orElseThrow(() -> new InventoryNotFoundException("Inventory Not Found"));

		if (inventory.getReservedQty() < quantity) {
			throw new IllegalStateException("Reserved quantity is less than requested quantity.");
		}

		inventory.setReservedQty(inventory.getReservedQty() - quantity);

		inventory.setAvailableQty(inventory.getAvailableQty() + quantity);

		inventoryRepository.save(inventory);
	}
}
