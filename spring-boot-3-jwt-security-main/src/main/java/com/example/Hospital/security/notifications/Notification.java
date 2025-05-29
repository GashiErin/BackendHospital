package com.example.Hospital.security.notifications;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "notifications")
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "NotificationID")
    private Integer id;

    @Column(name = "PacientID")
    private Integer patientId;

    @Column(name = "MjekuID")
    private Integer doctorId;

    @Column(name = "Seen")
    private Boolean seen = false;

    @Column(name = "Created", insertable = false, updatable = false)
    private LocalDateTime created;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "read_status")
    private Boolean readStatus;

    @Column(name = "message", nullable = false)
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private NotificationType type;

    @Column(name = "recipient_id")
    private Integer recipientId;
}