package com.fulfillment.product_service.dto;

import lombok.Data;

@Data
public class ProductInput {

	private String productSku;
	private String productName;
	private String productDesc;
	private String productCategory;
	private Double productPrice;
}
