package com.example.Hospital.security.chat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageDTO {
    private Integer senderId;
    private Integer receiverId;
    private String content;}
