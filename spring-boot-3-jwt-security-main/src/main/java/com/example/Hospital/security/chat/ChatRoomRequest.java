package com.example.Hospital.security.chat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomRequest {
    private Integer userId;
    private Integer professionalId;
}