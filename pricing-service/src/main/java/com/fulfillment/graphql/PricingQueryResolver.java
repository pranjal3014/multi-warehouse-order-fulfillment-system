package com.fulfillment.graphql;

import java.util.List;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import com.fulfillment.dto.response.PricingResponse;
import com.fulfillment.service.PricingService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class PricingQueryResolver {

	private final PricingService pricingService;

	@QueryMapping
	public List<PricingResponse> pricings() {
		return pricingService.getAllPricings();
	}

	@QueryMapping
	public PricingResponse pricingById(@Argument Long pricingId) {

		return pricingService.getPricingById(pricingId);
	}

	@QueryMapping
	public PricingResponse pricingByProduct(@Argument Long productId) {

		return pricingService.getPricingByProduct(productId);
	}
}