package com.fulfillment.dto.request;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class PricingRequest {

    private Long productId;

    private BigDecimal basePrice;

    private BigDecimal discountPercentage;

    private BigDecimal taxPercentage;

    private Boolean active;
}