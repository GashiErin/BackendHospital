package com.example.Hospital.security.chat;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Integer> {
    List<Message> findByChatRoomIdOrderBySentAtAsc(Long chatRoomId);
    List<Message> findBySenderIdAndReceiverId(Integer senderId, Integer receiverId);

    @Modifying
    @Transactional
    void deleteByChatRoomId(Long chatRoomId);

    @Modifying
    @Transactional
    @Query("DELETE FROM Message m WHERE m.sender.id = :userId OR m.receiver.id = :userId")
    void deleteAllByUserId(Integer userId);
}