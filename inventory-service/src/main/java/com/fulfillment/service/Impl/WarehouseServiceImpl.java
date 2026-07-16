package com.fulfillment.service.Impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.fulfillment.dto.request.WarehouseRequest;
import com.fulfillment.dto.response.WarehouseResponse;
import com.fulfillment.entity.Warehouse;
import com.fulfillment.exception.WarehouseNotFoundException;
import com.fulfillment.mapper.InventoryMapper;
import com.fulfillment.repository.WarehouseRepository;
import com.fulfillment.service.WarehouseService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WarehouseServiceImpl implements WarehouseService {

	private final WarehouseRepository warehouseRepository;
	private final InventoryMapper mapper;

	@Override
	public WarehouseResponse createWarehouse(WarehouseRequest request) {
		if (warehouseRepository.existsBywCode(request.getWCode())) {
			throw new RuntimeException("warehouse already exists");
		}
		Warehouse warehouse = mapper.toWarehouseEntity(request);

		Warehouse savedWarehouse = warehouseRepository.save(warehouse);

		return mapper.toWarehouseResponse(savedWarehouse);
	}

	@Override
	public WarehouseResponse getWarehouseById(Long warehouseId) {
		Warehouse warehouse = warehouseRepository.findById(warehouseId)
				.orElseThrow(() -> new WarehouseNotFoundException("Warehouse Not Found"));

		return mapper.toWarehouseResponse(warehouse);
	}

	@Override
	public List<WarehouseResponse> getAllWarehouses() {
		List<Warehouse> warehouses = warehouseRepository.findAll();
		return warehouses.stream().map(mapper::toWarehouseResponse).toList();
	}

	@Override
	public WarehouseResponse updateWarehouse(Long warehouseId, WarehouseRequest request) {
		Warehouse warehouse = warehouseRepository.findById(warehouseId)
				.orElseThrow(() -> new RuntimeException("Warehouse Not Found"));
		warehouse.setWName(request.getWName());
		warehouse.setWCode(request.getWCode());
		warehouse.setWCity(request.getWCity());
		warehouse.setWState(request.getWState());
		warehouse.setWAddress(request.getWAddress());
		Warehouse updatedWarehouse = warehouseRepository.save(warehouse);
		return mapper.toWarehouseResponse(updatedWarehouse);
	}

	@Override
	public Boolean deleteWarehouse(Long warehouseId) {
		warehouseRepository.deleteById(
                warehouseId);

        return true;
	}

}
