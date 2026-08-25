package com.example.couplead.chat.dto.response;

import java.time.Instant;

import com.example.couplead.chat.domain.MessageType;

public record ChatMessageResponse(
                Long coupleId,
                Long senderId,
                String senderNickname,

                MessageType type,
                String content,
                Instant sentAt,

                boolean deleted,
                Instant deletedAt,

                Long replyToMessageId,
                String replyToSenderNickname,
                MessageType replyToType,
                String replyToContent,
                String clientMessageId,

                String mediaGroupId) {
}