package com.fulfillment.grpc.server;

import org.springframework.grpc.server.service.GrpcService;

import com.fulfillment.cart.grpc.CartItem;
import com.fulfillment.cart.grpc.CartOperationResponse;
import com.fulfillment.cart.grpc.CartRequest;
import com.fulfillment.cart.grpc.CartResponse;
import com.fulfillment.cart.grpc.CartServiceGrpc;
import com.fulfillment.dto.response.CartItemResponse;
import com.fulfillment.service.CartService;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;

@GrpcService
@RequiredArgsConstructor
public class CartGrpcServer extends CartServiceGrpc.CartServiceImplBase {

    private final CartService cartService;

    @Override
    public void getCartByUserId(CartRequest request,
                                StreamObserver<CartResponse> responseObserver) {

        com.fulfillment.dto.response.CartResponse cart =
                cartService.getCartByUserId(request.getUserId());

        CartResponse.Builder builder = CartResponse.newBuilder()
                .setCartId(cart.getCartId())
                .setUserId(cart.getUserId());

        for (CartItemResponse item : cart.getCartItems()) {

            builder.addItems(
                    CartItem.newBuilder()
                            .setProductId(item.getProductId())
                            .setQuantity(item.getQuantity())
                            .build()
            );
        }

        responseObserver.onNext(builder.build());
        responseObserver.onCompleted();
    }

    @Override
    public void clearCart(CartRequest request,
                          StreamObserver<CartOperationResponse> responseObserver) {

        Boolean success = cartService.clearCart(request.getUserId());

        CartOperationResponse response =
                CartOperationResponse.newBuilder()
                        .setSuccess(success)
                        .setMessage("Cart cleared successfully.")
                        .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}