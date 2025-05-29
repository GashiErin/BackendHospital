package com.example.Hospital.security.notifications;

import com.example.Hospital.security.user.User;
import com.example.Hospital.security.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final com.example.Hospital.security.notifications.NotificationService notificationService;
    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<com.example.Hospital.security.notifications.Notification>> getUserNotifications(Authentication authentication) {
        User user = userService.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        return ResponseEntity.ok(notificationService.getUserNotifications(user.getId()));
    }

    @GetMapping("/unread-count")
    public ResponseEntity<Long> getUnreadNotificationsCount(Authentication authentication) {
        User user = userService.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        return ResponseEntity.ok(notificationService.getUnreadNotificationsCount(user.getId()));
    }

    @PutMapping("/{notificationId}/mark-read")
    public ResponseEntity<com.example.Hospital.security.notifications.Notification> markNotificationAsRead(
            @PathVariable Integer notificationId,
            Authentication authentication) {
        User user = userService.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        return ResponseEntity.ok(notificationService.markAsRead(notificationId));
    }

    @PutMapping("/mark-all-read")
    public ResponseEntity<Void> markAllNotificationsAsRead(Authentication authentication) {
        User user = userService.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        notificationService.markAllAsRead(user.getId());
        return ResponseEntity.ok().build();
    }
}