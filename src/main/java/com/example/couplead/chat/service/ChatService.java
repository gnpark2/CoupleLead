package com.example.couplead.chat.service;

import java.util.Optional;

import com.example.couplead.chat.dto.response.ChatAnnouncementResponse;
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

        void deleteMessage(
                        Long userId,
                        Long messageId);

        ChatAnnouncementResponse setAnnouncement(
                        Long userId,
                        Long coupleId,
                        Long messageId);

        Optional<ChatAnnouncementResponse> getAnnouncement(
                        Long userId,
                        Long coupleId);

        void removeAnnouncement(
                        Long userId,
                        Long coupleId);
}
