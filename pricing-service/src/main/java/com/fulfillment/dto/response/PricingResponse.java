package com.fulfillment.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PricingResponse {

    private Long pricingId;

    private Long productId;

    private BigDecimal basePrice;

    private BigDecimal discountPercentage;

    private BigDecimal taxPercentage;

    private BigDecimal finalPrice;

    private Boolean active;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}