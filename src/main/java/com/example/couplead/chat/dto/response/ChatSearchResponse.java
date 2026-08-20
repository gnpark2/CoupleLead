package com.example.couplead.chat.dto.response;

import java.time.Instant;
import java.time.LocalDateTime;

public record ChatSearchResponse(
        Long messageId,
        Long senderId,
        String senderNickname,
        String content,
        Instant sentAt) {
}