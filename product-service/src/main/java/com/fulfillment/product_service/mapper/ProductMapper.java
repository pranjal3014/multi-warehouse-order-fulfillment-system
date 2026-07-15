package com.fulfillment.product_service.mapper;

import org.springframework.stereotype.Component;

import com.fulfillment.product_service.dto.ProductInput;
import com.fulfillment.product_service.dto.ProductResponse;
import com.fulfillment.product_service.entity.Product;

@Component
public class ProductMapper {

	public Product convertToEntity(ProductInput request) {
		return Product.builder()
				.productSku(request.getProductSku())
				.productName(request.getProductName())
				.productDesc(request.getProductDesc())
				.productCategory(request.getProductCategory())
				.productPrice(request.getProductPrice())
				.build();
	}
	
	public ProductResponse convertToDto(Product product) {
		return ProductResponse.builder()
				.productId(product.getProductId())
				.productSku(product.getProductSku())
				.productName(product.getProductName())
				.productDesc(product.getProductDesc())
				.productCategory(product.getProductCategory())
				.productPrice(product.getProductPrice())
				.productActive(product.getActive())
				.createdAt(product.getCreatedAt())
				.updatedAt(product.getUpdatedAt())
				.build();
	}
}
