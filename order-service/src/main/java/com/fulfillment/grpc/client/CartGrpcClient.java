package com.fulfillment.grpc.client;

import org.springframework.stereotype.Component;

import com.fulfillment.cart.grpc.CartOperationResponse;
import com.fulfillment.cart.grpc.CartRequest;
import com.fulfillment.cart.grpc.CartResponse;
import com.fulfillment.cart.grpc.CartServiceGrpc;

import io.grpc.ManagedChannel;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CartGrpcClient {

    private final ManagedChannel cartManagedChannel;

    public CartResponse getCartByUserId(Long userId) {

        CartServiceGrpc.CartServiceBlockingStub stub =
                CartServiceGrpc.newBlockingStub(cartManagedChannel);

        CartRequest request = CartRequest.newBuilder()
                .setUserId(userId)
                .build();

        return stub.getCartByUserId(request);
    }

    public CartOperationResponse clearCart(Long userId) {

        CartServiceGrpc.CartServiceBlockingStub stub =
                CartServiceGrpc.newBlockingStub(cartManagedChannel);

        CartRequest request = CartRequest.newBuilder()
                .setUserId(userId)
                .build();

        return stub.clearCart(request);
    }

}