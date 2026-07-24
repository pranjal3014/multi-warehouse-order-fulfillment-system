package com.fulfillment.dto.request;

import lombok.Data;

@Data
public class PlaceOrderRequest {

    private Long userId;

    private String paymentMethod;

}
