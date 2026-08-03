package com.fulfillment.grpc;

import org.springframework.grpc.server.service.GrpcService;

import com.fulfillment.dto.response.PricingResponse;
import com.fulfillment.pricing.grpc.PriceRequest;
import com.fulfillment.pricing.grpc.PriceResponse;
import com.fulfillment.pricing.grpc.PricingServiceGrpc;
import com.fulfillment.service.PricingService;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;

@GrpcService
@RequiredArgsConstructor
public class PricingGrpcService extends PricingServiceGrpc.PricingServiceImplBase {

    private final PricingService pricingService;

    @Override
    public void getPriceByProductId(PriceRequest request, StreamObserver<PriceResponse> responseObserver) {

        PricingResponse pricing = pricingService.getPricingByProduct(request.getProductId());

        PriceResponse response = PriceResponse.newBuilder()
                .setProductId(pricing.getProductId())
                .setBasePrice(pricing.getBasePrice().doubleValue())
                .setDiscountPercentage(pricing.getDiscountPercentage().doubleValue())
                .setTaxPercentage(pricing.getTaxPercentage().doubleValue())
                .setFinalPrice(pricing.getFinalPrice().doubleValue())
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}