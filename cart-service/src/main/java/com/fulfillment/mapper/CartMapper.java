package com.fulfillment.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import com.fulfillment.dto.response.CartItemResponse;
import com.fulfillment.dto.response.CartResponse;
import com.fulfillment.entity.Cart;
import com.fulfillment.entity.CartItem;

@Component
public class CartMapper {

    public CartResponse mapToCartResponse(Cart cart) {

        return CartResponse.builder()
                .cartId(cart.getCartId())
                .userId(cart.getUserId())
                .cartItems(mapToCartItemResponseList(cart.getCartItems()))
                .createdAt(cart.getCreatedAt())
                .updatedAt(cart.getUpdatedAt())
                .build();
    }

    public List<CartItemResponse> mapToCartItemResponseList(List<CartItem> cartItems) {

        return cartItems.stream()
                .map(this::mapToCartItemResponse)
                .toList();
    }

    public CartItemResponse mapToCartItemResponse(CartItem cartItem) {

        return CartItemResponse.builder()
                .cartItemId(cartItem.getCartItemId())
                .productId(cartItem.getProductId())
                .quantity(cartItem.getQuantity())
                .build();
    }

}