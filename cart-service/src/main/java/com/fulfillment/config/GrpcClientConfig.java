package com.fulfillment.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

@Configuration
public class GrpcClientConfig {

    @Bean
    public ManagedChannel inventoryManagedChannel() {

        return ManagedChannelBuilder
                .forAddress("localhost", 9092) // Inventory Service gRPC Port
                .usePlaintext()
                .build();
    }

    @Bean
    public ManagedChannel pricingManagedChannel() {

        return ManagedChannelBuilder
                .forAddress("localhost", 9091) //Pricing Service gRPC Port
                .usePlaintext()
                .build();
    }
}