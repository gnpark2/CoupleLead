package com.example.couplead.chat.service;

import com.example.couplead.chat.dto.response.ChatHistoryPageResponse;

public interface ChatService {
    ChatHistoryPageResponse getMessages(
            Long userId,
            Long coupleId,
            Long beforeMessageId,
            int size);

    void markAsRead(
            Long userId,
            Long coupleId);
}
