package com.fulfillment.service.Impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.fulfillment.dto.request.PricingRequest;
import com.fulfillment.dto.response.PricingResponse;
import com.fulfillment.entity.Pricing;
import com.fulfillment.exception.PricingNotFoundException;
import com.fulfillment.exception.ProductPricingAlreadyExistsException;
import com.fulfillment.mapper.PricingMapper;
import com.fulfillment.repository.PricingRepository;
import com.fulfillment.service.PricingService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PricingServiceImpl implements PricingService {

	private final PricingRepository pricingRepository;
	private final PricingMapper mapper;

	@Override
	public PricingResponse createPricing(PricingRequest request) {

		if (pricingRepository.existsByProductId(request.getProductId())) {

			throw new ProductPricingAlreadyExistsException("Pricing already exists for product");
		}

		Pricing pricing = Pricing.builder().productId(request.getProductId()).basePrice(request.getBasePrice())
				.discountPercentage(request.getDiscountPercentage()).taxPercentage(request.getTaxPercentage())
				.active(request.getActive()).build();

		Pricing savedPricing = pricingRepository.save(pricing);

		return mapper.toResponse(savedPricing);
	}

	@Override
	public PricingResponse updatePricing(Long productId, PricingRequest request) {

		Pricing pricing = pricingRepository.findByProductId(productId)
				.orElseThrow(() -> new PricingNotFoundException("Product not found"));

		pricing.setBasePrice(request.getBasePrice());

		pricing.setDiscountPercentage(request.getDiscountPercentage());

		pricing.setTaxPercentage(request.getTaxPercentage());

		pricing.setActive(request.getActive());

		Pricing updatedPricing = pricingRepository.save(pricing);

		return mapper.toResponse(updatedPricing);
	}

	@Override
	public PricingResponse getPricingById(Long pricingId) {

		Pricing pricing = pricingRepository.findById(pricingId)
				.orElseThrow(() -> new PricingNotFoundException("Pricing not found"));

		return mapper.toResponse(pricing);
	}

	@Override
	public PricingResponse getPricingByProduct(Long productId) {

		Pricing pricing = pricingRepository.findByProductId(productId)
				.orElseThrow(() -> new PricingNotFoundException("Pricing not found"));

		return mapper.toResponse(pricing);
	}

	@Override
	public List<PricingResponse> getAllPricings() {

		return pricingRepository.findAll().stream().map(mapper::toResponse).toList();
	}

	@Override
	public Boolean deletePricing(Long pricingId) {

		Pricing pricing = pricingRepository.findById(pricingId)
				.orElseThrow(() -> new PricingNotFoundException("Pricing not found"));

		pricing.setActive(false);;

		return true;
	}

}
