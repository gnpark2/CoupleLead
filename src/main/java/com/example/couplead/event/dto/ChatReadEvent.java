package com.example.couplead.event.dto;

import java.time.Instant;

public record ChatReadEvent(
    Long coupleId,
    Long readerId,
    Instant readAt
) {
    
}
