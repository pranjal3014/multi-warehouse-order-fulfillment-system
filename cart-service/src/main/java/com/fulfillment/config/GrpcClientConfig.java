package com.fulfillment.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

@Configuration
public class GrpcClientConfig {

    @Value("${inventory.grpc.host:localhost}")
    private String inventoryGrpcHost;

    @Value("${pricing.grpc.host:localhost}")
    private String pricingGrpcHost;

    @Bean
    public ManagedChannel inventoryManagedChannel() {

        return ManagedChannelBuilder
                .forAddress(inventoryGrpcHost, 9095) // Inventory Service gRPC Port
                .usePlaintext()
                .build();
    }

    @Bean
    public ManagedChannel pricingManagedChannel() {

        return ManagedChannelBuilder
                .forAddress(pricingGrpcHost, 9091) //Pricing Service gRPC Port
                .usePlaintext()
                .build();
    }
}
