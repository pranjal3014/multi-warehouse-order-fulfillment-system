package com.fulfillment.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "pricing")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pricing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pricingId;

    @Column(nullable = false, unique = true)
    private Long productId;

    @Column(nullable = false)
    private BigDecimal basePrice;

    @Column(nullable = false)
    private BigDecimal discountPercentage;

    @Column(nullable = false)
    private BigDecimal taxPercentage;

    @Column(nullable = false)
    private Boolean active;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}