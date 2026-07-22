package com.fulfillment.grpc.client;

import org.springframework.stereotype.Component;

import com.fulfillment.inventory.grpc.InventoryListResponse;
import com.fulfillment.inventory.grpc.InventoryRequest;
import com.fulfillment.inventory.grpc.InventoryServiceGrpc;

import io.grpc.ManagedChannel;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class InventoryGrpcClient {

    private final ManagedChannel inventoryManagedChannel;

    public InventoryListResponse checkInventory(Long productId) {

        InventoryServiceGrpc.InventoryServiceBlockingStub stub =
                InventoryServiceGrpc.newBlockingStub(inventoryManagedChannel);

        InventoryRequest request = InventoryRequest.newBuilder()
                .setProductId(productId)
                .build();

        return stub.checkInventory(request);
    }
}