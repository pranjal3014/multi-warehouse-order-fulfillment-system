package com.fulfillment.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.graphql.client.HttpGraphQlClient;
@Configuration
public class GraphQlClientConfig {

    @Bean
    public HttpGraphQlClient paymentGraphQlClient() {

        WebClient webClient = WebClient.builder()
                .baseUrl("http://localhost:8087/graphql")
                .build();

        return HttpGraphQlClient.builder(webClient).build();
    }
}