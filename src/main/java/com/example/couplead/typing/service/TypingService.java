package com.example.couplead.typing.service;

public interface TypingService {
    void typing(
        Long userId,
        Long coupleId,
        String nickname
    );

    boolean isTyping(Long userId);
}
