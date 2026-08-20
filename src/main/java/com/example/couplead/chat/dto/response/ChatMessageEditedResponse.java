package com.example.couplead.chat.dto.response;

import java.time.Instant;

public record ChatMessageEditedResponse(
        Long coupleId,
        Long messageId,
        String content,
        Instant editedAt) {
}