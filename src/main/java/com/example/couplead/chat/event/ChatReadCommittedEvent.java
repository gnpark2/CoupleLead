package com.example.couplead.chat.event;

import com.example.couplead.event.dto.ChatReadEvent;

public record ChatReadCommittedEvent(
        ChatReadEvent event
) {
}