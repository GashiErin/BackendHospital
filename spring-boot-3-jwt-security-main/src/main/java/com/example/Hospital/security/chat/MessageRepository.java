package com.example.Hospital.security.chat;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Integer> {
    List<Message> findByChatRoomIdOrderBySentAtAsc(Long chatRoomId);  // Changed to Long
    List<Message> findBySenderIdAndReceiverId(Integer senderId, Integer receiverId);

    void deleteByChatRoomId(Long chatRoomId);
}