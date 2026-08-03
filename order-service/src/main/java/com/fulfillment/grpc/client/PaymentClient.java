package com.fulfillment.grpc.client;

import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.beans.factory.annotation.Value;

import com.fulfillment.dto.payment.PaymentRequest;
import com.fulfillment.dto.payment.PaymentResponse;
import com.fulfillment.dto.request.RefundRequest;
import com.fulfillment.entity.PaymentStatus;
import com.fulfillment.exception.PaymentFailedException;

import lombok.RequiredArgsConstructor;

@Component
public class PaymentClient {

    private final RestClient restClient;

    public PaymentClient(@Value("${payment.service.url:http://localhost:8087/graphql}") String paymentServiceUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(paymentServiceUrl)
                .build();
    }
    
    
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
    
    @SuppressWarnings("unchecked")
    public PaymentResponse refundPayment(RefundRequest refundRequest) {

        String mutation = """
            mutation($refundRequest: RefundRequest!) {
              refundPayment(refundRequest: $refundRequest) {
                paymentId
                paymentStatus
                transactionId
              }
            }
            """;

        Map<String, Object> body = Map.of(
                "query", mutation,
                "variables", Map.of(
                        "refundRequest", refundRequest));

        Map<String, Object> response = restClient.post()
                .contentType(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .body(Map.class);

        if (response.containsKey("errors")) {
            throw new PaymentFailedException("Refund failed.");
        }

        Map<String, Object> data =
                (Map<String, Object>) response.get("data");

        Map<String, Object> payment =
                (Map<String, Object>) data.get("refundPayment");

        return PaymentResponse.builder()
                .paymentId(Long.valueOf(payment.get("paymentId").toString()))
                .paymentStatus(
                        PaymentStatus.valueOf(payment.get("paymentStatus").toString())
                )
                .transactionId(payment.get("transactionId").toString())
                .build();
    }
}
