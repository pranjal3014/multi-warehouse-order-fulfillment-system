package com.fulfillment.service;

import java.util.List;

import com.fulfillment.dto.request.PricingRequest;
import com.fulfillment.dto.response.PricingResponse;

public interface PricingService {

	PricingResponse createPricing(PricingRequest request);

	PricingResponse updatePricing(Long productId, PricingRequest request);

	PricingResponse getPricingById(Long pricingId);

	PricingResponse getPricingByProduct(Long productId);

	List<PricingResponse> getAllPricings();

	Boolean deletePricing(Long pricingId);
}