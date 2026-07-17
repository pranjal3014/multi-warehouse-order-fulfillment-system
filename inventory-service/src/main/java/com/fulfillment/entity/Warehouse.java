package com.fulfillment.entity;

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
@Table(name="warehouses")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Warehouse {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long wId;
	
	@Column(name="warehouse_name")
	private String wName;
	
	@Column(name="warehouse_code")
	private String wCode;
	
	@Column(name="warehouse_city")
	private String wCity;
	
	@Column(name="warehouse_state")
	private String wState;
	
	@Column(name="warehouse_address")
	private String wAddress;
	
	@Column(name="warehouse_active_status")
	private Boolean wActive;
	
	@CreationTimestamp
	private LocalDateTime createdAt;
	
	@UpdateTimestamp
	private LocalDateTime updatedAt;
}
