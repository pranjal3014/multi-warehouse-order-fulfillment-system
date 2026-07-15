package com.fulfillment.product_service.graphql;

import java.util.List;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import com.fulfillment.product_service.dto.ProductResponse;
import com.fulfillment.product_service.service.ProductService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ProductQueryResolver {

	private final ProductService productService;
	
	@QueryMapping(name="products")
	public List<ProductResponse> getProducts(){
		return productService.getAllProduct();
	}
	
	@QueryMapping
	public ProductResponse productById(@Argument Long id) {
		return productService.getProductById(id);
	}
	
	@QueryMapping
	public ProductResponse productBySku(@Argument("productSku") String sku) {
		return productService.getProductBySku(sku);
	}
}
