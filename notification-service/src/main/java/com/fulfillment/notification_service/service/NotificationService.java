package com.fulfillment.notification_service.service;

import com.fulfillment.notification_service.dto.response.NotificationResponse;
import com.fulfillment.notification_service.enums.NotificationEventType;

public interface NotificationService {

    NotificationResponse createNotification(
            Long orderId,
            Long userId,
            NotificationEventType eventType,
            String message);

}
