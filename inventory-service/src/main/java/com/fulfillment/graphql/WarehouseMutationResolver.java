package com.fulfillment.graphql;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

import com.fulfillment.dto.request.WarehouseRequest;
import com.fulfillment.dto.response.WarehouseResponse;
import com.fulfillment.service.WarehouseService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class WarehouseMutationResolver {

	private final WarehouseService warehouseService;

	@MutationMapping
	public WarehouseResponse createWarehouse(@Argument WarehouseRequest warehouse) {

		return warehouseService.createWarehouse(warehouse);
	}

	@MutationMapping
	public WarehouseResponse updateWarehouse(@Argument Long warehouseId, @Argument WarehouseRequest warehouse) {

		return warehouseService.updateWarehouse(warehouseId, warehouse);
	}

	@MutationMapping
	public Boolean deleteWarehouse(@Argument Long warehouseId) {

		return warehouseService.deleteWarehouse(warehouseId);
	}
}
