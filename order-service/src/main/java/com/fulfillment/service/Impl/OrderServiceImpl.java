package com.fulfillment.service.Impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fulfillment.cart.grpc.CartItem;
import com.fulfillment.cart.grpc.CartResponse;
import com.fulfillment.dto.request.CancelOrderRequest;
import com.fulfillment.dto.request.PlaceOrderRequest;
import com.fulfillment.dto.response.OrderProcessingResult;
import com.fulfillment.dto.response.OrderResponse;
import com.fulfillment.entity.Order;
import com.fulfillment.entity.OrderItem;
import com.fulfillment.entity.OrderStatus;
import com.fulfillment.entity.PaymentStatus;
import com.fulfillment.exception.OrderNotFoundException;
import com.fulfillment.grpc.client.CartGrpcClient;
import com.fulfillment.grpc.client.InventoryGrpcClient;
import com.fulfillment.grpc.client.PricingGrpcClient;
import com.fulfillment.inventory.grpc.InventoryItem;
import com.fulfillment.inventory.grpc.InventoryListResponse;
import com.fulfillment.mapper.OrderMapper;
import com.fulfillment.pricing.grpc.PriceResponse;
import com.fulfillment.repository.OrderRepository;
import com.fulfillment.service.OrderService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {

	private final OrderRepository orderRepository;

	private final OrderMapper orderMapper;

	private final CartGrpcClient cartGrpcClient;

	private final InventoryGrpcClient inventoryGrpcClient;

	private final PricingGrpcClient pricingGrpcClient;

	@Override
	public OrderResponse placeOrder(PlaceOrderRequest request) {

	    // 1. Fetch Cart
	    CartResponse cartResponse = fetchCart(request.getUserId());

	    // 2. Create Order
	    Order order = createOrder(request.getUserId());

	    // 3. Process Cart Items
	    OrderProcessingResult processingResult = processCartItems(cartResponse, order);

	    // 4. Set Order Details
	    order.setOrderItems(processingResult.getOrderItems());
	    order.setTotalAmount(processingResult.getTotalAmount());

	    // 5. Save Order
	    Order savedOrder = orderRepository.save(order);

	    // 6. Clear Cart
	    cartGrpcClient.clearCart(request.getUserId());

	    // 7. Return Response
	    return orderMapper.toOrderResponse(savedOrder);
	}

	@Override
	public Boolean cancelOrder(CancelOrderRequest request) {

		Order order = orderRepository.findById(request.getOrderId())
				.orElseThrow(() -> new OrderNotFoundException("Order not found with id : " + request.getOrderId()));

		if (order.getOrderStatus() == OrderStatus.CANCELLED) {
			throw new RuntimeException("Order is already cancelled.");
		}

		order.setOrderStatus(OrderStatus.CANCELLED);

		orderRepository.save(order);

		return true;
	}

	@Override
	@Transactional(readOnly = true)
	public List<OrderResponse> getOrders() {

		return orderRepository.findAll().stream().map(orderMapper::toOrderResponse).toList();
	}

	@Override
	@Transactional(readOnly = true)
	public OrderResponse getOrderById(Long orderId) {

		Order order = orderRepository.findById(orderId)
				.orElseThrow(() -> new OrderNotFoundException("Order not found with id : " + orderId));

		return orderMapper.toOrderResponse(order);
	}

	@Override
	@Transactional(readOnly = true)
	public List<OrderResponse> getOrdersByUser(Long userId) {

		return orderRepository.findByUserId(userId).stream().map(orderMapper::toOrderResponse).toList();
	}

	private CartResponse fetchCart(Long userId) {

		CartResponse cartResponse = cartGrpcClient.getCartByUserId(userId);

		if (cartResponse.getItemsList().isEmpty()) {
			throw new RuntimeException("Cart is empty.");
		}

		return cartResponse;
	}

	private Order createOrder(Long userId) {

		return Order.builder().userId(userId).orderStatus(OrderStatus.PENDING).paymentStatus(PaymentStatus.PENDING)
				.build();
	}

	private OrderProcessingResult processCartItems(CartResponse cartResponse, Order order) {

		List<OrderItem> orderItems = new ArrayList<>();

		BigDecimal totalAmount = BigDecimal.ZERO;

		for (CartItem cartItem : cartResponse.getItemsList()) {

			InventoryListResponse inventory = inventoryGrpcClient.checkInventory(cartItem.getProductId());

			InventoryItem selectedInventory = inventory.getInventoriesList().stream()
					.filter(item -> item.getAvailableQuantity() >= cartItem.getQuantity()).findFirst()
					.orElseThrow(() -> new RuntimeException("Inventory not available"));

			PriceResponse price = pricingGrpcClient.getPriceByProductId(cartItem.getProductId());

			OrderItem orderItem = OrderItem.builder().order(order).productId(cartItem.getProductId())
					.warehouseId(selectedInventory.getWarehouseId()).quantity(cartItem.getQuantity())
					.price(BigDecimal.valueOf(price.getFinalPrice())).build();

			orderItems.add(orderItem);

			BigDecimal itemTotal = BigDecimal.valueOf(price.getFinalPrice())
					.multiply(BigDecimal.valueOf(cartItem.getQuantity()));

			totalAmount = totalAmount.add(itemTotal);
		}

		return new OrderProcessingResult(orderItems, totalAmount);
	}
}
