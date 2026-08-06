package com.example.couplead.chat.dto.response;

import java.time.LocalDateTime;

public record ChatMessageResponse(
    Long coupleId,
    Long senderId,
    String senderNickname,
    String content,
    LocalDateTime sentAt
) {
    
}
