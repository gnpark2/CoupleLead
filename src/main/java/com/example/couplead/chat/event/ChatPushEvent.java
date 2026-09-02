package com.example.couplead.chat.event;

import com.example.couplead.chat.domain.MessageType;

public record ChatPushEvent(
        Long coupleId,
        Long senderId,
        Long receiverId,
        String senderNickname,
        MessageType type,
        String content) {
}