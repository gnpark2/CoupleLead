package com.example.couplead.chat.realtime;

import com.fasterxml.jackson.databind.JsonNode;

public record ChatRealtimeEvent(
        String eventId,
        ChatRealtimeEventType type,
        Long coupleId,
        JsonNode payload) {
}