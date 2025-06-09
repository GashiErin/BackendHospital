package com.example.Hospital.security.chat;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    Optional<ChatRoom> findByUser_IdAndProfessional_Id(Integer userId, Integer professionalId);
    List<ChatRoom> findByUser_Id(Integer userId);
    List<ChatRoom> findByProfessional_Id(Integer professionalId);

    @Modifying
    @Transactional
    @Query("DELETE FROM ChatRoom c WHERE c.user.id = :userId OR c.professional.id = :userId")
    void deleteAllByUserId(Integer userId);
}