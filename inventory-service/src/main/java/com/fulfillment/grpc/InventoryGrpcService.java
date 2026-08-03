package com.fulfillment.grpc;

import java.util.List;

import org.springframework.grpc.server.service.GrpcService;

import com.fulfillment.dto.response.InventoryResponse;
import com.fulfillment.inventory.grpc.InventoryItem;
import com.fulfillment.inventory.grpc.InventoryListResponse;
import com.fulfillment.inventory.grpc.InventoryRequest;
import com.fulfillment.inventory.grpc.InventoryServiceGrpc;
import com.fulfillment.inventory.grpc.ReleaseInventoryRequest;
import com.fulfillment.inventory.grpc.ReleaseInventoryResponse;
import com.fulfillment.inventory.grpc.ReserveInventoryRequest;
import com.fulfillment.inventory.grpc.ReserveInventoryResponse;
import com.fulfillment.service.InventoryService;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;

@GrpcService
@RequiredArgsConstructor
public class InventoryGrpcService extends InventoryServiceGrpc.InventoryServiceImplBase {

	private final InventoryService inventoryService;

	@Override
	public void checkInventory(InventoryRequest request, StreamObserver<InventoryListResponse> responseObserver) {
		try {
			List<InventoryResponse> inventories = inventoryService.getInventoryByProduct(request.getProductId());

			if (inventories == null || inventories.isEmpty()) {
				responseObserver.onError(Status.NOT_FOUND.withDescription("Inventory not found").asRuntimeException());
				return;
			}

			InventoryListResponse.Builder responseBuilder = InventoryListResponse.newBuilder();

			for (InventoryResponse inventory : inventories) {

				InventoryItem item = InventoryItem.newBuilder().setInventoryId(inventory.getInventoryId())
						.setProductId(inventory.getProductId()).setWarehouseId(inventory.getWarehouseId())
						.setAvailableQuantity(inventory.getAvailableQyt()).setAvailable(inventory.getAvailableQyt() > 0)
						.build();

				responseBuilder.addInventories(item);
			}

			responseObserver.onNext(responseBuilder.build());
			responseObserver.onCompleted();
		} catch (Exception e) {
			e.printStackTrace();
			responseObserver.onError(io.grpc.Status.INTERNAL.withDescription(e.getMessage()).asRuntimeException());
			return;
		}
	}

	@Override
	public void reserveInventory(ReserveInventoryRequest request,
			StreamObserver<ReserveInventoryResponse> responseObserver) {

		try {

			inventoryService.reserveInventory(request.getProductId(), request.getWarehouseId(), request.getQuantity());

			ReserveInventoryResponse response = ReserveInventoryResponse.newBuilder().setSuccess(true)
					.setMessage("Inventory Reserved Successfully").build();

			responseObserver.onNext(response);
			responseObserver.onCompleted();

		} catch (Exception e) {

			ReserveInventoryResponse response = ReserveInventoryResponse.newBuilder().setSuccess(false)
					.setMessage(e.getMessage()).build();

			responseObserver.onNext(response);
			responseObserver.onCompleted();
		}
	}

	@Override
	public void releaseInventory(ReleaseInventoryRequest request,
			StreamObserver<ReleaseInventoryResponse> responseObserver) {

		try {

			inventoryService.releaseInventory(request.getProductId(), request.getWarehouseId(), request.getQuantity());

			ReleaseInventoryResponse response = ReleaseInventoryResponse.newBuilder().setSuccess(true)
					.setMessage("Inventory Released Successfully").build();

			responseObserver.onNext(response);
			responseObserver.onCompleted();

		} catch (Exception e) {

			ReleaseInventoryResponse response = ReleaseInventoryResponse.newBuilder().setSuccess(false)
					.setMessage(e.getMessage()).build();

			responseObserver.onNext(response);
			responseObserver.onCompleted();
		}
	}
}