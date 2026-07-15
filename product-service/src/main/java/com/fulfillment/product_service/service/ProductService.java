package com.fulfillment.product_service.service;

import java.util.List;

import com.fulfillment.product_service.dto.ProductInput;
import com.fulfillment.product_service.dto.ProductResponse;

public interface ProductService {
	List<ProductResponse> getAllProduct();
	ProductResponse getProductById(Long id);
	ProductResponse getProductBySku(String sku);
	ProductResponse createProduct(ProductInput request);
	ProductResponse updateProduct(Long id, ProductInput request);
	void deleteProduct(Long id);
}
