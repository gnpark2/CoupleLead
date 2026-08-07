package com.example.couplead.event.dto;

import java.time.LocalDateTime;

public record ChatReadEvent(
    Long coupleId,
    Long readerId,
    LocalDateTime readAt
) {
    
}
