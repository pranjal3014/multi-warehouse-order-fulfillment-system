package com.fulfillment.dto.response;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderItemResponse {

    private Long orderItemId;

    private Long productId;

    private Long warehouseId;

    private Integer quantity;

    private BigDecimal price;

}