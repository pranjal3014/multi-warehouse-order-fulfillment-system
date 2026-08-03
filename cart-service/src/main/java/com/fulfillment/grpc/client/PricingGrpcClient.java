package com.fulfillment.grpc.client;

import org.springframework.stereotype.Component;

import com.fulfillment.pricing.grpc.PriceRequest;
import com.fulfillment.pricing.grpc.PriceResponse;
import com.fulfillment.pricing.grpc.PricingServiceGrpc;

import io.grpc.ManagedChannel;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PricingGrpcClient {

    private final ManagedChannel pricingManagedChannel;

    public PriceResponse getPriceByProductId(Long productId) {

        PricingServiceGrpc.PricingServiceBlockingStub stub =
                PricingServiceGrpc.newBlockingStub(pricingManagedChannel);

        PriceRequest request = PriceRequest.newBuilder()
                .setProductId(productId)
                .build();

        return stub.getPriceByProductId(request);
    }
}