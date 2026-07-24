package com.fulfillment.notification_service.dto.event;

import java.math.BigDecimal;

import com.fulfillment.notification_service.enums.NotificationEventType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentSuccessEvent {

    @Builder.Default
    private NotificationEventType eventType = NotificationEventType.PAYMENT_SUCCESS;

    private Long orderId;

    private Long userId;

    private BigDecimal amount;

}
