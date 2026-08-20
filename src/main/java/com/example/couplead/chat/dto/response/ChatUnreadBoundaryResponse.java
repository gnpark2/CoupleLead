package com.example.couplead.chat.dto.response;

public record ChatUnreadBoundaryResponse(
        Long firstUnreadMessageId,
        long unreadCount) {
}