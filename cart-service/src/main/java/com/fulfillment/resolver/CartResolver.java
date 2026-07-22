package com.fulfillment.resolver;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import com.fulfillment.dto.request.AddToCartRequest;
import com.fulfillment.dto.request.UpdateCartRequest;
import com.fulfillment.dto.response.CartResponse;
import com.fulfillment.service.CartService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class CartResolver {

    private final CartService cartService;

    @QueryMapping
    public CartResponse getCart(@Argument Long userId) {
        return cartService.getCart(userId);
    }

    @MutationMapping
    public CartResponse addToCart(@Argument AddToCartRequest request) {
        return cartService.addToCart(request);
    }

    @MutationMapping
    public CartResponse updateCart(@Argument Long cartItemId,
                                   @Argument UpdateCartRequest request) {
        return cartService.updateCart(cartItemId, request);
    }

    @MutationMapping
    public Boolean removeFromCart(@Argument Long cartItemId) {
        return cartService.removeFromCart(cartItemId);
    }

    @MutationMapping
    public Boolean clearCart(@Argument Long userId) {
        return cartService.clearCart(userId);
    }
}