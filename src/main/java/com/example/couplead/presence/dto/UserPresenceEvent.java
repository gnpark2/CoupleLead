package com.example.couplead.presence.dto;

import java.time.LocalDateTime;

public record UserPresenceEvent(
    Long userId,
    boolean online,
    LocalDateTime timestamp
) {
    
}
