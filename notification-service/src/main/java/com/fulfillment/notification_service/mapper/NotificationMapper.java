package com.fulfillment.notification_service.mapper;

import org.springframework.stereotype.Component;

import com.fulfillment.notification_service.dto.response.NotificationResponse;
import com.fulfillment.notification_service.entity.Notification;

@Component
public class NotificationMapper {

    public NotificationResponse toResponse(Notification notification) {

        return NotificationResponse.builder()
                .notificationId(notification.getNotificationId())
                .orderId(notification.getOrderId())
                .userId(notification.getUserId())
                .eventType(notification.getEventType())
                .message(notification.getMessage())
                .notificationStatus(notification.getNotificationStatus())
                .createdAt(notification.getCreatedAt())
                .build();
    }

}
