package com.example.couplead.chat.dto.request;

public record ChatMessageRequest(
    Long coupleId,
    String content
) {
    
}
