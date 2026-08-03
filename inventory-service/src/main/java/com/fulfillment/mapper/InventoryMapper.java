package com.fulfillment.mapper;

import org.springframework.stereotype.Component;

import com.fulfillment.dto.request.WarehouseRequest;
import com.fulfillment.dto.response.InventoryResponse;
import com.fulfillment.dto.response.WarehouseResponse;
import com.fulfillment.entity.Inventory;
import com.fulfillment.entity.Warehouse;

@Component
public class InventoryMapper {
	
    public Warehouse toWarehouseEntity(
            WarehouseRequest request) {

        return Warehouse.builder()
                .wName(request.getWName())
                .wCode(request.getWCode())
                .wCity(request.getWCity())
                .wState(request.getWState())
                .wAddress(request.getWAddress())
                .build();
    }

    public WarehouseResponse toWarehouseResponse(
            Warehouse warehouse) {

        return WarehouseResponse.builder()
                .wId(warehouse.getWId())
                .wName(warehouse.getWName())
                .wCode(warehouse.getWCode())
                .wCity(warehouse.getWCity())
                .wState(warehouse.getWState())
                .wAddress(warehouse.getWAddress())
                .wActive(warehouse.getWActive())
                .build();
    }

    public InventoryResponse toInventoryResponse(
            Inventory inventory) {

        return InventoryResponse.builder()
                .inventoryId(inventory.getInventoryId())
                .productId(inventory.getProductId())
                .warehouseId(
                        inventory.getWarehouse()
                                .getWId())
                .warehouseName(
                        inventory.getWarehouse()
                                .getWName())
                .availableQyt(
                        inventory.getAvailableQty())
                .reservedQyt(
                        inventory.getReservedQty())
                .build();
    }

}
