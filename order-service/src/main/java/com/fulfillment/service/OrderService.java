package com.fulfillment.service;

import java.util.List;

import com.fulfillment.dto.request.CancelOrderRequest;
import com.fulfillment.dto.request.PlaceOrderRequest;
import com.fulfillment.dto.response.OrderResponse;

public interface OrderService {

    OrderResponse placeOrder(PlaceOrderRequest request);

    OrderResponse cancelOrder(CancelOrderRequest request);

    List<OrderResponse> getOrders();

    OrderResponse getOrderById(Long orderId);

    List<OrderResponse> getOrdersByUser(Long userId);

}