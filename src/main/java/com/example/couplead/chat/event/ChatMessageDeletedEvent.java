package com.example.couplead.chat.event;

import java.time.Instant;

import com.example.couplead.chat.domain.MessageType;

public record ChatMessageDeletedEvent(
        Long coupleId,
        Long messageId,
        MessageType type,
        String imagePath,
        Instant deletedAt) {
}