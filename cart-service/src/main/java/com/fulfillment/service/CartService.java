package com.fulfillment.service;

import com.fulfillment.dto.request.AddToCartRequest;
import com.fulfillment.dto.request.UpdateCartRequest;
import com.fulfillment.dto.response.CartResponse;

public interface CartService {

    CartResponse addToCart(AddToCartRequest request);

    CartResponse getCart(Long userId);

    CartResponse updateCart(Long cartItemId, UpdateCartRequest request);

    Boolean removeFromCart(Long cartItemId);

    Boolean clearCart(Long userId);

}