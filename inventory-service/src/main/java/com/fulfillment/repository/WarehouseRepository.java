package com.fulfillment.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fulfillment.entity.Warehouse;

public interface WarehouseRepository extends JpaRepository<Warehouse, Long>{
	Optional<Warehouse> findByWCode(String wCode);
	boolean existsBywCode(String wCode);
}
