package com.fulfillment.product_service.entity;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="products")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Product {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long productId;
	
	@Column(name="sku")
	private String productSku;
	
	@Column(name="name")
	private String productName;
	
	@Column(name="description")
	private String productDesc;
	
	@Column(name="category")
	private String productCategory;
	
	@Column(name="price")
	private Double productPrice;
	
	@Column(name="active_status")
	private Boolean active;
	
	@CreationTimestamp
	private LocalDateTime createdAt;
	
	@UpdateTimestamp
	private LocalDateTime updatedAt;
}
