package com.fulfillment.product_service.service.Impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;

import com.fulfillment.product_service.dto.ProductInput;
import com.fulfillment.product_service.dto.ProductResponse;
import com.fulfillment.product_service.entity.Product;
import com.fulfillment.product_service.exception.ProductAlredayExistsException;
import com.fulfillment.product_service.exception.ProductNotFoundException;
import com.fulfillment.product_service.mapper.ProductMapper;
import com.fulfillment.product_service.repository.ProductRepository;
import com.fulfillment.product_service.service.ProductService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService{
	private final ProductRepository productRepository;
	private final ProductMapper mapper;

	@Override
	@Cacheable(value = "products")
	public List<ProductResponse> getAllProduct() {
		List<Product> products = productRepository.findAll();
		return products.stream()
				.map(mapper::convertToDto)
				.toList();
	}

	@Override
	@Cacheable(value = "product", key = "#id")
	public ProductResponse getProductById(Long id) {
		Product product = productRepository.findById(id).orElseThrow(()->new ProductNotFoundException("Product Not Found!!!"));
		return mapper.convertToDto(product);
	}

	@Override
	@Cacheable(value = "product", key = "#sku")
	public ProductResponse getProductBySku(String sku) {
		Product product = productRepository.findByProductSku(sku).orElseThrow(()->new ProductNotFoundException("Product Not Found with this sku id: "+sku));
		return mapper.convertToDto(product);
	}

	@Override
	@CacheEvict(value = { "products", "product" }, allEntries = true)
	public ProductResponse createProduct(ProductInput request) {
		if(productRepository.existsByProductSku(request.getProductSku())) {
			throw new ProductAlredayExistsException("Sku Already Exists");
		}
		Product product = mapper.convertToEntity(request);
		Product saveProduct = productRepository.save(product);
		return mapper.convertToDto(saveProduct);
	}

	@Override
	@CacheEvict(value = { "products", "product" }, allEntries = true)
	public ProductResponse updateProduct(Long id, ProductInput request) {
		Product product = productRepository.findById(id).orElseThrow(()->new ProductNotFoundException("Product Not Found!!!"));
		product.setProductSku(request.getProductSku());
		product.setProductName(request.getProductName());
		product.setProductCategory(request.getProductCategory());
		product.setProductDesc(request.getProductDesc());
		product.setProductPrice(request.getProductPrice());;
		Product updatedProduct = productRepository.save(product);
		return mapper.convertToDto(updatedProduct);
	}

	@Override
	@CacheEvict(value = { "products", "product" }, allEntries = true)
    public void deleteProduct(Long id) {

        Product product = productRepository.findById(id)
                .orElseThrow(() ->
                        new ProductNotFoundException("Product not found with id : " + id));

        productRepository.delete(product);
    }
}
