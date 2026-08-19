package com.example.couplead.chat.dto.response;

import java.time.LocalDateTime;

public record ChatMessageEditedResponse(
        Long coupleId,
        Long messageId,
        String content,
        LocalDateTime editedAt) {
}