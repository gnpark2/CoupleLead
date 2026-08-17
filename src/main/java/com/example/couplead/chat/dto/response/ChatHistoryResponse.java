package com.example.couplead.chat.dto.response;

import java.time.LocalDateTime;

import com.example.couplead.chat.domain.MessageType;

public record ChatHistoryResponse(
    Long messageId,
    Long senderId,
    String senderNickname,
    MessageType type,
    String content,
    LocalDateTime sentAt,
    LocalDateTime readAt,
    boolean deleted,
    LocalDateTime deletedAt
) {
    
}
