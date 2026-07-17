package com.fulfillment.graphql;

import java.util.List;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import com.fulfillment.dto.response.InventoryResponse;
import com.fulfillment.service.InventoryService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class InventoryQueryResolver {

	private final InventoryService inventoryService;

	@QueryMapping
	public InventoryResponse inventoryById(@Argument Long inventoryId) {

		return inventoryService.getInventoryById(inventoryId);
	}

	@QueryMapping
	public List<InventoryResponse> inventoryByProduct(@Argument Long productId) {

		return inventoryService.getInventoryByProduct(productId);
	}
}
