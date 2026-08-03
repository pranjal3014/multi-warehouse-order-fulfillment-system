package com.fulfillment.grpc.client;

import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.beans.factory.annotation.Value;

@Component
public class ShipmentClient {

    private final RestClient restClient;

    public ShipmentClient(@Value("${shipment.service.url:http://localhost:8088/graphql}") String shipmentServiceUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(shipmentServiceUrl)
                .build();
    }

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
