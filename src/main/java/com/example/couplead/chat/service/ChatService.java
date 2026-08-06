package com.example.couplead.chat.service;

import java.util.List;

import com.example.couplead.chat.dto.response.ChatHistoryResponse;

public interface ChatService {
    List<ChatHistoryResponse> getMessages(
        Long userId,
        Long coupleId
    );
}
