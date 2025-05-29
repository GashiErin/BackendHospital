package com.example.Hospital.security.notification;

import com.example.Hospital.security.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final com.example.Hospital.security.notification.NotificationRepository notificationRepository;

    @Transactional
    public com.example.Hospital.security.notification.Notification createNotification(User recipient, String message, com.example.Hospital.security.notification.NotificationType type) {
        com.example.Hospital.security.notification.Notification notification = com.example.Hospital.security.notification.Notification.builder()
                .recipient(recipient)
                .message(message)
                .type(type)
                .isRead(false)
                .build();
        return notificationRepository.save(notification);
    }

    @Transactional(readOnly = true)
    public List<com.example.Hospital.security.notification.Notification> getUserNotifications(Integer userId) {
        return notificationRepository.findByRecipientIdOrderByCreatedAtDesc(userId);
    }

    @Transactional(readOnly = true)
    public long getUnreadNotificationsCount(Integer userId) {
        return notificationRepository.countUnreadNotifications(userId);
    }

    @Transactional
    public com.example.Hospital.security.notification.Notification markAsRead(Integer notificationId) {
        com.example.Hospital.security.notification.Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        notification.setRead(true);
        return notificationRepository.save(notification);
    }

    @Transactional
    public void markAllAsRead(Integer userId) {
        List<com.example.Hospital.security.notification.Notification> unreadNotifications = notificationRepository
                .findByRecipientIdAndIsReadOrderByCreatedAtDesc(userId, false);
        unreadNotifications.forEach(notification -> notification.setRead(true));
        notificationRepository.saveAll(unreadNotifications);
    }
}