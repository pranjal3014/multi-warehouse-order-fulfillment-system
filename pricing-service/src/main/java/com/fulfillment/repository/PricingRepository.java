package com.fulfillment.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fulfillment.entity.Pricing;

public interface PricingRepository extends JpaRepository<Pricing, Long> {

    Optional<Pricing> findByProductId(Long productId);

    boolean existsByProductId(Long productId);

    List<Pricing> findByActive(Boolean active);
}