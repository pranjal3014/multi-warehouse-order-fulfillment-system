package com.fulfillment.grpc.client;

import org.springframework.stereotype.Service;

import com.fulfillment.inventory.grpc.InventoryRequest;
import com.fulfillment.inventory.grpc.InventoryListResponse;
import com.fulfillment.inventory.grpc.InventoryServiceGrpc;
import com.fulfillment.inventory.grpc.ReleaseInventoryRequest;
import com.fulfillment.inventory.grpc.ReserveInventoryRequest;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

@Service
public class InventoryGrpcClient {

	private final InventoryServiceGrpc.InventoryServiceBlockingStub inventoryBlockingStub;

	public InventoryGrpcClient() {

		ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 9092).usePlaintext().build();

		inventoryBlockingStub = InventoryServiceGrpc.newBlockingStub(channel);
	}

	public InventoryListResponse checkInventory(Long productId) {

		InventoryRequest request = InventoryRequest.newBuilder().setProductId(productId).build();

		return inventoryBlockingStub.checkInventory(request);
	}

	public void reserveInventory(Long productId, Long warehouseId, Integer quantity) {

		ReserveInventoryRequest request = ReserveInventoryRequest.newBuilder().setProductId(productId)
				.setWarehouseId(warehouseId).setQuantity(quantity).build();

		inventoryBlockingStub.reserveInventory(request);
	}

	public void releaseInventory(Long productId, Long warehouseId, Integer quantity) {

		ReleaseInventoryRequest request = ReleaseInventoryRequest.newBuilder().setProductId(productId)
				.setWarehouseId(warehouseId).setQuantity(quantity).build();

		inventoryBlockingStub.releaseInventory(request);
	}

}
