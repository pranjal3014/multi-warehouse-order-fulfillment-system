package com.fulfillment.notification_service.service.Impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fulfillment.notification_service.dto.response.NotificationResponse;
import com.fulfillment.notification_service.entity.Notification;
import com.fulfillment.notification_service.enums.NotificationEventType;
import com.fulfillment.notification_service.enums.NotificationStatus;
import com.fulfillment.notification_service.mapper.NotificationMapper;
import com.fulfillment.notification_service.repository.NotificationRepository;
import com.fulfillment.notification_service.service.NotificationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;

    private final NotificationMapper notificationMapper;

    @Override
    public NotificationResponse createNotification(
            Long orderId,
            Long userId,
            NotificationEventType eventType,
            String message) {

        Notification notification = Notification.builder()
                .orderId(orderId)
                .userId(userId)
                .eventType(eventType)
                .message(message)
                .notificationStatus(NotificationStatus.PENDING)
                .build();

        Notification savedNotification = notificationRepository.save(notification);

        log.info("Notification sent. Event Type : {}, Order Id : {}, User Id : {}, Message : {}",
                eventType, orderId, userId, message);

        savedNotification.setNotificationStatus(NotificationStatus.SENT);

        Notification updatedNotification = notificationRepository.save(savedNotification);

        return notificationMapper.toResponse(updatedNotification);
    }

}
