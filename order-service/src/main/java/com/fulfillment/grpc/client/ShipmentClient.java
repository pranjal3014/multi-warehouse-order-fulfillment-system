package com.fulfillment.grpc.client;

import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class ShipmentClient {

    private final RestClient restClient = RestClient.builder()
            .baseUrl("http://localhost:8088/graphql")
            .build();

    public void createShipment(Long orderId, Long userId, Long warehouseId) {

        String mutation = """
            mutation($createShipmentRequest: CreateShipmentRequest!) {
              createShipment(createShipmentRequest: $createShipmentRequest) {
                shipmentId
              }
            }
            """;

        Map<String, Object> shipmentRequest = Map.of(
                "orderId", orderId,
                "userId", userId,
                "warehouseId", warehouseId);

        Map<String, Object> body = Map.of(
                "query", mutation,
                "variables", Map.of(
                        "createShipmentRequest", shipmentRequest));

        restClient.post()
                .contentType(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .body(Map.class);
    }
}
