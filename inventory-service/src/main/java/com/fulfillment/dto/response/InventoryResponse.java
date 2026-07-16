package com.fulfillment.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InventoryResponse {

	private Long inventoryId;
	private Long productId;
	private Long warehouseId;
	private String warehouseName;
	private Integer availableQyt;
	private Integer reservedQyt;
	
}
