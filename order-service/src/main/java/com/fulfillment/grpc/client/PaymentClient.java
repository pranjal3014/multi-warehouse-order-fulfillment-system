package com.fulfillment.grpc.client;

import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.fulfillment.dto.payment.PaymentRequest;
import com.fulfillment.dto.payment.PaymentResponse;
import com.fulfillment.entity.PaymentStatus;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PaymentClient {

    private final RestClient restClient = RestClient.builder()
            .baseUrl("http://localhost:8087/graphql")
            .build();
    
    
    @SuppressWarnings("unchecked")
    public PaymentResponse makePayment(PaymentRequest paymentRequest) {

        String mutation = """
            mutation($paymentRequest: PaymentRequest!) {
              makePayment(paymentRequest: $paymentRequest) {
                paymentId
                paymentStatus
                transactionId
              }
            }
            """;

        Map<String, Object> body = Map.of(
                "query", mutation,
                "variables", Map.of(
                        "paymentRequest", paymentRequest));

        Map<String, Object> response = restClient.post()
                .contentType(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .body(Map.class);

        Map<String, Object> data = (Map<String, Object>) response.get("data");
        Map<String, Object> payment = (Map<String, Object>) data.get("makePayment");

        return PaymentResponse.builder()
                .paymentId(Long.valueOf(payment.get("paymentId").toString()))
                .paymentStatus(
                	    PaymentStatus.valueOf(payment.get("paymentStatus").toString())
                	)
                .transactionId(payment.get("transactionId").toString())
                .build();
    }

}