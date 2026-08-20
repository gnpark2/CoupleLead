package com.example.couplead.chat.dto.request;

import com.example.couplead.chat.domain.MessageType;

public record ChatMessageRequest(
                Long coupleId,
                MessageType type,
                String content,
                Long replyToMessageId,
                String clientMessageId) {
}