package com.fulfillment.event;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentRefundEvent {

    private String eventType;

    private Long orderId;

    private Long userId;

    private BigDecimal amount;

}
