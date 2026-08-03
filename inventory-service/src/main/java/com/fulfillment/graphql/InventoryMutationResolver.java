package com.fulfillment.graphql;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

import com.fulfillment.dto.request.InventoryRequest;
import com.fulfillment.dto.response.InventoryResponse;
import com.fulfillment.service.InventoryService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class InventoryMutationResolver {

	private final InventoryService inventoryService;

	@MutationMapping
	public InventoryResponse createInventory(@Argument InventoryRequest inventory) {

		return inventoryService.createInventory(inventory);
	}

	@MutationMapping
	public InventoryResponse updateInventory(@Argument Long inventoryId, @Argument InventoryRequest inventory) {

		return inventoryService.updateInventory(inventoryId, inventory);
	}

	@MutationMapping
	public Boolean deleteInventory(@Argument Long inventoryId) {

		return inventoryService.deleteInventory(inventoryId);
	}
}
