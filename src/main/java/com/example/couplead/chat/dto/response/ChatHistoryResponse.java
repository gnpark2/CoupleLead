package com.example.couplead.chat.dto.response;

import java.time.Instant;

import com.example.couplead.chat.domain.MessageType;

public record ChatHistoryResponse(
                Long messageId,
                Long senderId,
                String senderNickname,
                MessageType type,
                String content,
                Instant sentAt,
                Instant readAt,
                boolean deleted,
                Instant deletedAt,
                boolean edited,
                Instant editedAt,
                Long replyToMessageId,
                String replyToSenderNickname,
                MessageType replyToType,
                String replyToContent,
                String clientMessageId,
                String mediaGroupId) {
}