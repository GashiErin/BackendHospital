package com.example.Hospital.security.notifications;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Integer> {
    List<Notification> findByRecipientIdOrderByCreatedAtDesc(Integer recipientId);

    List<Notification> findByRecipientIdAndReadStatusFalseOrderByCreatedAtDesc(Integer recipientId);

    long countByRecipientIdAndReadStatusFalse(Integer recipientId);
}