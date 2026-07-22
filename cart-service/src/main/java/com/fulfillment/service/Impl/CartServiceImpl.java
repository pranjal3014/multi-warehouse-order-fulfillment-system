package com.fulfillment.service.Impl;

import java.util.Optional;
import org.springframework.stereotype.Service;

import com.fulfillment.dto.request.AddToCartRequest;
import com.fulfillment.dto.request.UpdateCartRequest;
import com.fulfillment.dto.response.CartResponse;
import com.fulfillment.entity.Cart;
import com.fulfillment.entity.CartItem;
import com.fulfillment.exception.CartItemNotFoundException;
import com.fulfillment.exception.CartNotFoundException;
import com.fulfillment.mapper.CartMapper;
import com.fulfillment.repository.CartItemRepository;
import com.fulfillment.repository.CartRepository;
import com.fulfillment.service.CartService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {
	private final CartRepository cartRepository;

	private final CartItemRepository cartItemRepository;

	private final CartMapper cartMapper;

	@Override
	public CartResponse addToCart(AddToCartRequest request) {

		Cart cart = cartRepository.findByUserId(request.getUserId()).orElseGet(() -> {

			Cart newCart = Cart.builder().userId(request.getUserId()).build();

			return cartRepository.save(newCart);
		});

		Optional<CartItem> optionalCartItem = cartItemRepository.findByCartCartIdAndProductId(cart.getCartId(),
				request.getProductId());

		if (optionalCartItem.isPresent()) {

			CartItem cartItem = optionalCartItem.get();

			cartItem.setQuantity(cartItem.getQuantity() + request.getQuantity());

			cartItemRepository.save(cartItem);

		} else {

			CartItem cartItem = CartItem.builder().cart(cart).productId(request.getProductId())
					.quantity(request.getQuantity()).build();

			cartItemRepository.save(cartItem);

			cart.getCartItems().add(cartItem);
		}

		return cartMapper.mapToCartResponse(cart);
	}

	@Override
	@Transactional
	public CartResponse getCart(Long userId) {

		Cart cart = cartRepository.findByUserId(userId)
				.orElseThrow(() -> new CartNotFoundException("Cart not found for User Id : " + userId));

		return cartMapper.mapToCartResponse(cart);
	}

	@Override
	public CartResponse updateCart(Long cartItemId, UpdateCartRequest request) {

		CartItem cartItem = cartItemRepository.findById(cartItemId)
				.orElseThrow(() -> new CartItemNotFoundException("Cart Item not found with Id : " + cartItemId));

		cartItem.setQuantity(request.getQuantity());

		cartItemRepository.save(cartItem);

		return cartMapper.mapToCartResponse(cartItem.getCart());
	}

	@Override
	public Boolean removeFromCart(Long cartItemId) {

		CartItem cartItem = cartItemRepository.findById(cartItemId)
				.orElseThrow(() -> new CartItemNotFoundException("Cart Item not found with Id : " + cartItemId));

		cartItemRepository.delete(cartItem);

		return true;
	}

	@Override
	public Boolean clearCart(Long userId) {

		Cart cart = cartRepository.findByUserId(userId)
				.orElseThrow(() -> new CartNotFoundException("Cart not found for User Id : " + userId));

		cartItemRepository.deleteByCartCartId(cart.getCartId());

		return true;
	}

}
