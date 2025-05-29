package com.example.Hospital.security.notification;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NotificationRepository extends JpaRepository<com.example.Hospital.security.notification.Notification, Integer> {
    List<com.example.Hospital.security.notification.Notification> findByRecipientIdOrderByCreatedAtDesc(Integer recipientId);

    @Query("SELECT COUNT(n) FROM Notification n WHERE n.recipient.id = :userId AND n.isRead = false")
    long countUnreadNotifications(@Param("userId") Integer userId);

    List<com.example.Hospital.security.notification.Notification> findByRecipientIdAndIsReadOrderByCreatedAtDesc(Integer recipientId, boolean isRead);
}