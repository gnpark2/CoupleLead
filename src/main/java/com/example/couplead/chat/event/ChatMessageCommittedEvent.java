package com.example.couplead.chat.event;

import com.example.couplead.chat.dto.response.ChatMessageResponse;

public record ChatMessageCommittedEvent(
        Long coupleId,
        ChatMessageResponse message
) {
}