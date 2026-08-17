package com.example.couplead.chat.dto.response;

import java.time.LocalDateTime;

public record ChatAnnouncementResponse(
        Long id,
        Long messageId,

        Long createdBy,
        String createdByNickname,

        Long messageSenderId,
        String messageSenderNickname,

        String content,

        LocalDateTime messageSentAt,
        LocalDateTime createdAt) {
}