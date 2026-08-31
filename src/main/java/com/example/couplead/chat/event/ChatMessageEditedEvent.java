package com.example.couplead.chat.event;

import java.time.Instant;

public record ChatMessageEditedEvent(
        Long coupleId,
        Long messageId,
        String content,
        Instant editedAt) {
}