package com.fulfillment.grpc;

import java.util.List;

import org.springframework.grpc.server.service.GrpcService;

import com.fulfillment.dto.response.InventoryResponse;
import com.fulfillment.inventory.grpc.InventoryItem;
import com.fulfillment.inventory.grpc.InventoryListResponse;
import com.fulfillment.inventory.grpc.InventoryRequest;
import com.fulfillment.inventory.grpc.InventoryServiceGrpc;
import com.fulfillment.service.InventoryService;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;

@GrpcService
@RequiredArgsConstructor
public class InventoryGrpcService extends InventoryServiceGrpc.InventoryServiceImplBase {

    private final InventoryService inventoryService;

    @Override
    public void checkInventory(
            InventoryRequest request,
            StreamObserver<InventoryListResponse> responseObserver) {

        List<InventoryResponse> inventories =
                inventoryService.getInventoryByProduct(request.getProductId());

        if (inventories == null || inventories.isEmpty()) {
            responseObserver.onError(
                    Status.NOT_FOUND
                            .withDescription("Inventory not found")
                            .asRuntimeException());
            return;
        }

        InventoryListResponse.Builder responseBuilder =
                InventoryListResponse.newBuilder();

        for (InventoryResponse inventory : inventories) {

            InventoryItem item = InventoryItem.newBuilder()
                    .setInventoryId(inventory.getInventoryId())
                    .setProductId(inventory.getProductId())
                    .setWarehouseId(inventory.getWarehouseId())
                    .setAvailableQuantity(inventory.getAvailableQyt())
                    .setAvailable(inventory.getAvailableQyt() > 0)
                    .build();

            responseBuilder.addInventories(item);
        }

        responseObserver.onNext(responseBuilder.build());
        responseObserver.onCompleted();
    }
}