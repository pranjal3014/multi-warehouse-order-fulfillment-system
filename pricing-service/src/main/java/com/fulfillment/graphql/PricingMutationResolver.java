package com.fulfillment.graphql;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

import com.fulfillment.dto.request.PricingRequest;
import com.fulfillment.dto.response.PricingResponse;
import com.fulfillment.service.PricingService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class PricingMutationResolver {

	private final PricingService pricingService;

	@MutationMapping
	public PricingResponse createPricing(@Argument PricingRequest pricing) {

		return pricingService.createPricing(pricing);
	}

	@MutationMapping
	public PricingResponse updatePricing(@Argument Long pricingId, @Argument PricingRequest pricing) {

		return pricingService.updatePricing(pricingId, pricing);
	}

	@MutationMapping
	public Boolean deletePricing(@Argument Long pricingId) {

		return pricingService.deletePricing(pricingId);
	}
}
