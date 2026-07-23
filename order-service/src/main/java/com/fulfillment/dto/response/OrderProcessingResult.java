package com.fulfillment.dto.response;

import java.math.BigDecimal;
import java.util.List;

import com.fulfillment.entity.OrderItem;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OrderProcessingResult {

    private List<OrderItem> orderItems;

    private BigDecimal totalAmount;

}