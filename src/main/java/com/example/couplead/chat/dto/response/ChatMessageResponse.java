package com.example.couplead.chat.dto.response;

import java.time.LocalDateTime;

import com.example.couplead.chat.domain.MessageType;

public record ChatMessageResponse(
    Long coupleId,
    Long senderId,
    String senderNickname,
    MessageType type,
    String content,
    LocalDateTime sentAt,
    boolean deleted,
    LocalDateTime deletedAt
) {
    
}
