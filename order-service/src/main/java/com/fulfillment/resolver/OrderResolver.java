package com.fulfillment.resolver;

import java.util.List;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import com.fulfillment.dto.request.CancelOrderRequest;
import com.fulfillment.dto.request.PlaceOrderRequest;
import com.fulfillment.dto.response.OrderResponse;
import com.fulfillment.service.OrderService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class OrderResolver {

    private final OrderService orderService;

    @MutationMapping
    public OrderResponse placeOrder(@Argument PlaceOrderRequest request) {
        return orderService.placeOrder(request);
    }

    @MutationMapping
    public Boolean cancelOrder(@Argument CancelOrderRequest request) {
        return orderService.cancelOrder(request) != null;
    }

    @QueryMapping
    public List<OrderResponse> orders() {
        return orderService.getOrders();
    }

    @QueryMapping
    public OrderResponse orderById(@Argument Long orderId) {
        return orderService.getOrderById(orderId);
    }

    @QueryMapping
    public List<OrderResponse> ordersByUser(@Argument Long userId) {
        return orderService.getOrdersByUser(userId);
    }
}
