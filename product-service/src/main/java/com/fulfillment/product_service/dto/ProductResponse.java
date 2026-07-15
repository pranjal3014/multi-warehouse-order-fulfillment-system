package com.fulfillment.product_service.dto;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductResponse {

	private Long productId;
	private String productSku;
	private String productName;
	private String productDesc;
	private String productCategory;
	private Double productPrice;
	private Boolean productActive;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	
}
