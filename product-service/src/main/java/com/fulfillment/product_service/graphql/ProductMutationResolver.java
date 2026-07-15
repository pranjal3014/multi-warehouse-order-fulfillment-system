package com.fulfillment.product_service.graphql;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

import com.fulfillment.product_service.dto.ProductInput;
import com.fulfillment.product_service.dto.ProductResponse;
import com.fulfillment.product_service.service.ProductService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ProductMutationResolver {

	private final ProductService productService;
	
	@MutationMapping
	public ProductResponse createProduct(@Argument ProductInput product) {
		return productService.createProduct(product);
	}
	
	@MutationMapping
	public ProductResponse updateProduct(@Argument("productId") Long id, @Argument ProductInput product) {
		return productService.updateProduct(id, product);
	}
	
	@MutationMapping
	public Boolean deleteProduct(@Argument Long id) {
		productService.deleteProduct(id);
		return true;
	}
}
