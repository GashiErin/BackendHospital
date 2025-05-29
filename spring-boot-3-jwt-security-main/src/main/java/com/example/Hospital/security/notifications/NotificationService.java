package com.example.Hospital.security.notifications;

import com.example.Hospital.security.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;

    @Transactional
    public Notification createNotification(User recipient, String message, NotificationType type) {
        Notification notification = Notification.builder()
                .recipientId(recipient.getId())
                .message(message)
                .type(type)
                .seen(false)
                .readStatus(false)
                .createdAt(LocalDateTime.now())
                .build();
        return notificationRepository.save(notification);
    }

    @Transactional(readOnly = true)
    public List<Notification> getUserNotifications(Integer userId) {
        return notificationRepository.findByRecipientIdOrderByCreatedAtDesc(userId);
    }

    @Transactional(readOnly = true)
    public long getUnreadNotificationsCount(Integer userId) {
        return notificationRepository.countByRecipientIdAndReadStatusFalse(userId);
    }

    @Transactional
    public Notification markAsRead(Integer notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        notification.setReadStatus(true);
        notification.setSeen(true);
        return notificationRepository.save(notification);
    }

    @Transactional
    public void markAllAsRead(Integer userId) {
        List<Notification> unreadNotifications = notificationRepository
                .findByRecipientIdAndReadStatusFalseOrderByCreatedAtDesc(userId);
        unreadNotifications.forEach(notification -> {
            notification.setReadStatus(true);
            notification.setSeen(true);
        });
        notificationRepository.saveAll(unreadNotifications);
    }
}