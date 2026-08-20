package com.example.couplead.chat.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.example.couplead.chat.dto.response.ChatAnnouncementResponse;
import com.example.couplead.chat.dto.response.ChatHistoryPageResponse;
import com.example.couplead.chat.dto.response.ChatSearchPageResponse;
import com.example.couplead.chat.dto.response.ChatSearchResponse;
import com.example.couplead.chat.dto.response.ChatUnreadBoundaryResponse;

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

        void editMessage(
                        Long userId,
                        Long messageId,
                        String content);

        ChatSearchPageResponse searchMessages(
                        Long userId,
                        Long coupleId,
                        String keyword,
                        boolean useNori,
                        int size,
                        LocalDateTime beforeSentAt,
                        Long beforeMessageId);

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

        ChatUnreadBoundaryResponse getUnreadBoundary(
                        Long userId,
                        Long coupleId);
}
