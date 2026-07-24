package com.fulfillment.notification_service.dto.response;

import java.time.LocalDateTime;

import com.fulfillment.notification_service.enums.NotificationEventType;
import com.fulfillment.notification_service.enums.NotificationStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationResponse {

    private Long notificationId;

    private Long orderId;

    private Long userId;

    private NotificationEventType eventType;

    private String message;

    private NotificationStatus notificationStatus;

    private LocalDateTime createdAt;

}
