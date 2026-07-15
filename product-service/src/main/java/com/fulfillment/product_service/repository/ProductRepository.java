package com.fulfillment.product_service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fulfillment.product_service.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long>{
	Optional<Product> findByProductSku(String productSku);
	boolean existsByProductSku(String productSku);
}
