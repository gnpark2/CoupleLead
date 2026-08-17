package com.example.couplead.chat.dto.response;

import java.time.LocalDateTime;

public record ChatMessageDeletedResponse(
        Long coupleId,
        Long messageId,
        LocalDateTime deletedAt) {
}