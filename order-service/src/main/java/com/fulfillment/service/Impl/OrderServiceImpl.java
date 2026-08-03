package com.fulfillment.service.Impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fulfillment.cart.grpc.CartItem;
import com.fulfillment.cart.grpc.CartResponse;
import com.fulfillment.dto.payment.PaymentRequest;
import com.fulfillment.dto.payment.PaymentResponse;
import com.fulfillment.dto.request.CancelOrderRequest;
import com.fulfillment.dto.request.PlaceOrderRequest;
import com.fulfillment.dto.request.RefundRequest;
import com.fulfillment.dto.response.OrderProcessingResult;
import com.fulfillment.dto.response.OrderResponse;
import com.fulfillment.entity.Order;
import com.fulfillment.entity.OrderItem;
import com.fulfillment.entity.OrderStatus;
import com.fulfillment.entity.PaymentStatus;
import com.fulfillment.exception.InventoryUnavailableException;
import com.fulfillment.exception.OrderNotFoundException;
import com.fulfillment.exception.PaymentFailedException;
import com.fulfillment.grpc.client.CartGrpcClient;
import com.fulfillment.grpc.client.InventoryGrpcClient;
import com.fulfillment.grpc.client.PaymentClient;
import com.fulfillment.grpc.client.PricingGrpcClient;
import com.fulfillment.grpc.client.ShipmentClient;
import com.fulfillment.kafka.OrderEventProducer;
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
	private final PaymentClient paymentClient;
	private final PricingGrpcClient pricingGrpcClient;
	private final ShipmentClient shipmentClient;
	private final OrderEventProducer orderEventProducer;

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

	    // 6. Make Payment
	    PaymentRequest paymentRequest = PaymentRequest.builder()
	            .orderId(savedOrder.getOrderId())
	            .userId(savedOrder.getUserId())
	            .amount(savedOrder.getTotalAmount())
	            .paymentMethod(request.getPaymentMethod())
	            .build();

	    PaymentResponse paymentResponse = paymentClient.makePayment(paymentRequest);

	    PaymentStatus status = paymentResponse.getPaymentStatus();

	    if (status != PaymentStatus.PAID) {

	        savedOrder.setPaymentStatus(status);
	        savedOrder.setOrderStatus(OrderStatus.CANCELLED);
	        for (OrderItem item : savedOrder.getOrderItems()) {

	            inventoryGrpcClient.releaseInventory(
	                    item.getProductId(),
	                    item.getWarehouseId(),
	                    item.getQuantity());
	        }
	        orderRepository.save(savedOrder);

	        throw new PaymentFailedException("Payment failed.");
	    }

	    savedOrder.setPaymentId(paymentResponse.getPaymentId());
	    savedOrder.setTransactionId(paymentResponse.getTransactionId());
	    savedOrder.setPaymentStatus(status);
	    savedOrder.setOrderStatus(OrderStatus.CONFIRMED);

	    orderRepository.save(savedOrder);

	    orderEventProducer.publishOrderPlacedEvent(savedOrder);

	    // 9. Create Shipment
	    createShipments(savedOrder);

	    // 10. Clear Cart
	    cartGrpcClient.clearCart(request.getUserId());

	    // 11. Return Response
	    return orderMapper.toOrderResponse(savedOrder);
	}

	@Override
	@Transactional
	public OrderResponse cancelOrder(CancelOrderRequest request) {

	    // 1. Find Order
	    Order order = orderRepository.findById(request.getOrderId())
	            .orElseThrow(() -> new OrderNotFoundException("Order not found."));

	    // 2. Validate Order Status
	    if (order.getOrderStatus() == OrderStatus.CANCELLED) {
	        throw new RuntimeException("Order is already cancelled.");
	    }

	    // 3. Refund Payment
	    RefundRequest refundRequest = RefundRequest.builder()
	            .paymentId(order.getPaymentId())
	            .build();

	    PaymentResponse paymentResponse = paymentClient.refundPayment(refundRequest);

	    if (paymentResponse.getPaymentStatus() != PaymentStatus.REFUNDED) {
	        throw new PaymentFailedException("Refund failed.");
	    }

	    // 4. Update Order
	    order.setPaymentStatus(PaymentStatus.REFUNDED);
	    order.setOrderStatus(OrderStatus.CANCELLED);

	    // Optional
	    order.setTransactionId(paymentResponse.getTransactionId());

	    Order updatedOrder = orderRepository.save(order);

	    // 5. Release Inventory (Inventory Service)
	    for (OrderItem item : order.getOrderItems()) {

	        inventoryGrpcClient.releaseInventory(
	                item.getProductId(),
	                item.getWarehouseId(),
	                item.getQuantity());
	    }
	    

	    // 6. TODO: Publish Order Cancelled Event (Kafka)

	    return orderMapper.toOrderResponse(updatedOrder);
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

	private void createShipments(Order order) {

		order.getOrderItems().stream()
				.map(OrderItem::getWarehouseId)
				.distinct()
				.forEach(warehouseId -> shipmentClient.createShipment(
						order.getOrderId(), order.getUserId(), warehouseId));
	}

	private OrderProcessingResult processCartItems(CartResponse cartResponse, Order order) {

		List<OrderItem> orderItems = new ArrayList<>();

		BigDecimal totalAmount = BigDecimal.ZERO;

		for (CartItem cartItem : cartResponse.getItemsList()) {

			InventoryListResponse inventory = inventoryGrpcClient.checkInventory(cartItem.getProductId());

			InventoryItem selectedInventory = inventory.getInventoriesList().stream()
					.filter(item -> item.getAvailableQuantity() >= cartItem.getQuantity()).findFirst()
					.orElseThrow(() -> new InventoryUnavailableException("Inventory not available"));
			
			inventoryGrpcClient.reserveInventory(
			        cartItem.getProductId(),
			        selectedInventory.getWarehouseId(),
			        cartItem.getQuantity());
			
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
