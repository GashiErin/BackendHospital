package com.example.Hospital.security.chat;

import com.example.Hospital.security.user.User;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "chat_rooms")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class ChatRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "last_message_at")
    private LocalDateTime lastMessageAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "tokens", "authorities", "password"})
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "professional_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "tokens", "authorities", "password"})
    private User professional;

    @Column(name = "last_message_timestamp")
    private Long lastMessageTimestamp;

    @Column(name = "unread_count")
    private Integer unreadCount = 0;

    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Message> messages = new ArrayList<>();

    public void addMessage(Message message) {
        if (messages == null) {
            messages = new ArrayList<>();
        }
        messages.add(message);
        message.setChatRoom(this);
        this.lastMessageTimestamp = System.currentTimeMillis();
        this.lastMessageAt = LocalDateTime.now();

        if (!message.isRead()) {
            this.unreadCount = (this.unreadCount != null ? this.unreadCount : 0) + 1;
        }
    }

    public void removeMessage(Message message) {
        messages.remove(message);
        message.setChatRoom(null);
    }

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (lastMessageAt == null) {
            lastMessageAt = LocalDateTime.now();
        }
        if (lastMessageTimestamp == null) {
            lastMessageTimestamp = System.currentTimeMillis();
        }
        if (unreadCount == null) {
            unreadCount = 0;
        }
    }
}