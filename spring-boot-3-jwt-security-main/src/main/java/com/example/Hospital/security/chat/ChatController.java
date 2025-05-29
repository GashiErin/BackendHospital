package com.example.Hospital.security.chat;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import com.example.Hospital.security.user.User;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/chat")
@CrossOrigin(origins = "http://localhost:3000")
public class ChatController {
    private final ChatService chatService;

    @GetMapping("/rooms")
    public List<ChatRoom> getCurrentUserChats(@AuthenticationPrincipal User user) {
        return chatService.getUserChats(user.getId());
    }

    @PostMapping("/rooms/create")
    public ChatRoom createChatRoom(@RequestBody ChatRoomRequest request) {
        return chatService.createChatRoom(request.getUserId(), request.getProfessionalId());
    }

    @GetMapping("/messages/{chatRoomId}")
    public List<Message> getChatMessages(@PathVariable Long chatRoomId) {
        return chatService.getChatMessages(chatRoomId);
    }

    @PutMapping("/messages/{chatRoomId}/read")
    public void markMessagesAsRead(
            @PathVariable Long chatRoomId,
            @AuthenticationPrincipal User user
    ) {
        chatService.markMessagesAsRead(chatRoomId, user.getId());
    }

    @PostMapping("/messages/send")
    public Message sendMessage(@RequestBody ChatMessageDTO message) {
        return chatService.sendMessage(
                message.getSenderId(),
                message.getReceiverId(),
                message.getContent()
        );
    }
}