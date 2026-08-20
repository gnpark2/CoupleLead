package com.example.couplead.chat.dto.response;

import java.time.Instant;

public record ChatMessageDeletedResponse(
        Long coupleId,
        Long messageId,
        Instant deletedAt) {
}