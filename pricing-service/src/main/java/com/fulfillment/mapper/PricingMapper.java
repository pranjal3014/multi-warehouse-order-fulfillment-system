package com.fulfillment.mapper;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

import com.fulfillment.dto.response.PricingResponse;
import com.fulfillment.entity.Pricing;

@Component
public class PricingMapper {

	public PricingResponse toResponse(Pricing pricing) {

		BigDecimal discountAmount = pricing.getBasePrice().multiply(pricing.getDiscountPercentage())
				.divide(BigDecimal.valueOf(100));

		BigDecimal discountedPrice = pricing.getBasePrice().subtract(discountAmount);

		BigDecimal taxAmount = discountedPrice.multiply(pricing.getTaxPercentage()).divide(BigDecimal.valueOf(100));

		BigDecimal finalPrice = discountedPrice.add(taxAmount);

		return PricingResponse.builder().pricingId(pricing.getPricingId()).productId(pricing.getProductId())
				.basePrice(pricing.getBasePrice()).discountPercentage(pricing.getDiscountPercentage())
				.taxPercentage(pricing.getTaxPercentage()).finalPrice(finalPrice).active(pricing.getActive())
				.createdAt(pricing.getCreatedAt()).updatedAt(pricing.getUpdatedAt()).build();
	}
}