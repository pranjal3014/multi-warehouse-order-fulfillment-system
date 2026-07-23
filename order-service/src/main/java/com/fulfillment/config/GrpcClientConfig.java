package com.fulfillment.config;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GrpcClientConfig {

    @Bean
    ManagedChannel inventoryManagedChannel() {

        return ManagedChannelBuilder
                .forAddress("localhost", 9092)
                .usePlaintext()
                .build();
    }

    @Bean
    ManagedChannel pricingManagedChannel() {

        return ManagedChannelBuilder
                .forAddress("localhost", 9091)
                .usePlaintext()
                .build();
    }
                   
    @Bean
    ManagedChannel cartManagedChannel() {

        return ManagedChannelBuilder
                .forAddress("localhost", 9093)
                .usePlaintext()
                .build();
    }

}