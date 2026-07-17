package com.fulfillment.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class InventoryRequest {
	
	@NotNull
	private Long productId;
	@NotNull
	private Long WarehouseId;
	@Min(0)
	private Integer availableQyt;
}
