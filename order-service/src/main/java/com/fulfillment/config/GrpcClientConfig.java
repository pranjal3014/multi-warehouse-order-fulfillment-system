package com.fulfillment.config;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;

@Configuration
public class GrpcClientConfig {

    @Value("${inventory.grpc.host:localhost}")
    private String inventoryGrpcHost;

    @Value("${pricing.grpc.host:localhost}")
    private String pricingGrpcHost;

    @Value("${cart.grpc.host:localhost}")
    private String cartGrpcHost;

    @Bean
    ManagedChannel inventoryManagedChannel() {

        return ManagedChannelBuilder
                .forAddress(inventoryGrpcHost, 9095)
                .usePlaintext()
                .build();
    }

    @Bean
    ManagedChannel pricingManagedChannel() {

        return ManagedChannelBuilder
                .forAddress(pricingGrpcHost, 9091)
                .usePlaintext()
                .build();
    }
                   
    @Bean
    ManagedChannel cartManagedChannel() {

        return ManagedChannelBuilder
                .forAddress(cartGrpcHost, 9093)
                .usePlaintext()
                .build();
    }

}
