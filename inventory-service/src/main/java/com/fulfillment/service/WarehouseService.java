package com.fulfillment.service;

import java.util.List;

import com.fulfillment.dto.request.WarehouseRequest;
import com.fulfillment.dto.response.WarehouseResponse;

public interface WarehouseService {
	WarehouseResponse createWarehouse(WarehouseRequest request);

	WarehouseResponse getWarehouseById(Long warehouseId);

	List<WarehouseResponse> getAllWarehouses();

	WarehouseResponse updateWarehouse(Long warehouseId, WarehouseRequest request);

	Boolean deleteWarehouse(Long warehouseId);
}
