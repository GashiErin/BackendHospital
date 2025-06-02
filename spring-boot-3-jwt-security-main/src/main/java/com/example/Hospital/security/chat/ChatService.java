package com.example.Hospital.security.chat;

import com.example.Hospital.security.user.User;
import com.example.Hospital.security.user.UserRepository;
import com.example.Hospital.security.user.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatRoomRepository chatRoomRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;

    private boolean isHealthProfessional(Role role) {
        return role == Role.THERAPIST || role == Role.NUTRICIST;
    }

    public List<ChatRoom> getUserChats(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getRole() == Role.USER) {
            return chatRoomRepository.findByUser_Id(userId);
        } else if (isHealthProfessional(user.getRole())) {
            return chatRoomRepository.findByProfessional_Id(userId);
        }
        throw new RuntimeException("Invalid user role for chat");
    }

    public ChatRoom createChatRoom(Integer userId, Integer professionalId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        User professional = userRepository.findById(professionalId)
                .orElseThrow(() -> new RuntimeException("Professional not found"));

        if (user.getRole() != Role.USER) {
            throw new RuntimeException("First user must be a patient (USER role)");
        }

        if (!isHealthProfessional(professional.getRole())) {
            throw new RuntimeException("Second user must be a health professional (THERAPIST or NUTRICIST)");
        }

        Optional<ChatRoom> existingRoom = chatRoomRepository.findByUser_IdAndProfessional_Id(userId, professionalId);
        if (existingRoom.isPresent()) {
            return existingRoom.get();
        }

        ChatRoom chatRoom = ChatRoom.builder()
                .user(user)
                .professional(professional)
                .unreadCount(0)
                .build();

        return chatRoomRepository.save(chatRoom);
    }

    public List<Message> getChatMessages(Long chatRoomId) {
        return messageRepository.findByChatRoomIdOrderBySentAtAsc(chatRoomId);
    }

    @Transactional
    public Message sendMessage(Integer senderId, Integer receiverId, String content) {
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new RuntimeException("Sender not found"));
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new RuntimeException("Receiver not found"));

        // Find or create chat room
        ChatRoom chatRoom;
        if (sender.getRole() == Role.USER) {
            chatRoom = createChatRoom(sender.getId(), receiver.getId());
        } else {
            chatRoom = createChatRoom(receiver.getId(), sender.getId());
        }

        Message message = Message.builder()
                .chatRoom(chatRoom)
                .sender(sender)
                .receiver(receiver)
                .content(content)
                .build();

        message = messageRepository.save(message);

        // Update unread count
        chatRoom.setUnreadCount(chatRoom.getUnreadCount() + 1);
        chatRoomRepository.save(chatRoom);

        // Send real-time notification
        messagingTemplate.convertAndSendToUser(
                receiver.getId().toString(),
                "/queue/messages",
                message
        );

        return message;
    }

    @Transactional
    public void clearChatMessages(Long chatRoomId, Integer userId) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new RuntimeException("Chat room not found"));

        // Verify user is a participant in the chat
        if (!chatRoom.getUser().getId().equals(userId) &&
                !chatRoom.getProfessional().getId().equals(userId)) {
            throw new RuntimeException("User is not a participant in this chat room");
        }

        // Delete all messages in the chat room
        messageRepository.deleteByChatRoomId(chatRoomId);

        // Reset unread count
        chatRoom.setUnreadCount(0);
        chatRoomRepository.save(chatRoom);
    }


    @Transactional
    public void markMessagesAsRead(Long chatRoomId, Integer userId) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new RuntimeException("Chat room not found"));

        if (!chatRoom.getUser().getId().equals(userId) &&
                !chatRoom.getProfessional().getId().equals(userId)) {
            throw new RuntimeException("User is not a participant in this chat room");
        }

        chatRoom.setUnreadCount(0);
        chatRoomRepository.save(chatRoom);
    }
}