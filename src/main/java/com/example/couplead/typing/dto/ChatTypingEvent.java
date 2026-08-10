package com.example.couplead.typing.dto;

public record ChatTypingEvent(
    Long userId,
    Long coupleId,
    String nickname,
    boolean typing
) {
    
}
