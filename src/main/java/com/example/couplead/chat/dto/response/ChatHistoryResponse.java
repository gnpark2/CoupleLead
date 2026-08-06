package com.example.couplead.chat.dto.response;

import java.time.LocalDateTime;

public record ChatHistoryResponse(
    Long messageId,
    Long senderId,
    String senderNickname,
    String content,
    LocalDateTime sentAt,
    LocalDateTime readAt
) {
    
}
