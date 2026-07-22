package com.fulfillment.service;

import java.util.List;

import com.fulfillment.dto.request.InventoryRequest;
import com.fulfillment.dto.response.InventoryResponse;

public interface InventoryService {
	InventoryResponse createInventory(InventoryRequest request);

	InventoryResponse getInventoryById(Long inventoryId);

	List<InventoryResponse> getInventoryByProduct(Long productId);

	InventoryResponse updateInventory(Long inventoryId, InventoryRequest request);

	Boolean deleteInventory(Long inventoryId);
}
