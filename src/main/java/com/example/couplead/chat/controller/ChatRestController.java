package com.example.couplead.chat.controller;

import com.example.couplead.chat.service.ChatImageStorageService;
import com.example.couplead.chat.service.ChatSearchService;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;

import com.example.couplead.chat.dto.request.ChatMessageEditRequest;
import com.example.couplead.chat.dto.response.ChatAnnouncementResponse;
import com.example.couplead.chat.dto.response.ChatHistoryPageResponse;
import com.example.couplead.auth.security.CustomUserDetails;
import com.example.couplead.chat.document.MessageDocument;
import com.example.couplead.chat.dto.response.ChatImageUploadResponse;
import com.example.couplead.chat.dto.response.ChatSearchResponse;
import com.example.couplead.chat.service.ChatService;
import com.example.couplead.common.response.ApiResponse;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat")
public class ChatRestController {
        private final ChatSearchService chatSearchService;
        private final ChatService chatService;
        private final ChatImageStorageService chatImageStorageService;

        @GetMapping("/{coupleId}")
        public ApiResponse<ChatHistoryPageResponse> getMessages(
                        @AuthenticationPrincipal CustomUserDetails userDetails,
                        @PathVariable Long coupleId,
                        @RequestParam(required = false) Long beforeMessageId,
                        @RequestParam(defaultValue = "50") int size) {
                ChatHistoryPageResponse response = chatService.getMessages(
                                userDetails.getUser().getId(),
                                coupleId,
                                beforeMessageId,
                                size);

                return ApiResponse.success(response);
        }

        @PostMapping("/{coupleId}/read")
        public ApiResponse<Void> markAsRead(@AuthenticationPrincipal CustomUserDetails userDetails,
                        @PathVariable Long coupleId) {
                chatService.markAsRead(coupleId, userDetails.getUser().getId());

                return ApiResponse.success(null);
        }

        @GetMapping("/{coupleId}/search")
        public ApiResponse<List<ChatSearchResponse>> searchMessages(
                        @PathVariable Long coupleId,

                        @RequestParam String keyword,

                        @RequestParam(defaultValue = "true") boolean useNori,

                        @AuthenticationPrincipal CustomUserDetails userDetails) {
                return ApiResponse.success(
                                chatService.searchMessages(
                                                userDetails.getUser().getId(),
                                                coupleId,
                                                keyword,
                                                useNori));
        }

        @DeleteMapping("/messages/{messageId}")
        public ApiResponse<Void> deleteMessage(
                        @PathVariable Long messageId,
                        @AuthenticationPrincipal CustomUserDetails userDetails) {
                chatService.deleteMessage(
                                userDetails.getUser().getId(),
                                messageId);

                return ApiResponse.success(
                                null);
        }

        @PatchMapping("/messages/{messageId}")
        public ApiResponse<Void> editMessage(
                        @PathVariable Long messageId,
                        @RequestBody ChatMessageEditRequest request,
                        @AuthenticationPrincipal CustomUserDetails userDetails) {
                chatService.editMessage(
                                userDetails.getUser().getId(),
                                messageId,
                                request.content());

                return ApiResponse.success(null);
        }

        @PostMapping("/{coupleId}/announcement/{messageId}")
        public ApiResponse<ChatAnnouncementResponse> setAnnouncement(
                        @PathVariable Long coupleId,

                        @PathVariable Long messageId,

                        @AuthenticationPrincipal CustomUserDetails userDetails) {

                return ApiResponse.success(
                                chatService.setAnnouncement(
                                                userDetails.getUser().getId(),
                                                coupleId,
                                                messageId));
        }

        @GetMapping("/{coupleId}/announcement")
        public ApiResponse<ChatAnnouncementResponse> getAnnouncement(
                        @PathVariable Long coupleId,

                        @AuthenticationPrincipal CustomUserDetails userDetails) {

                return ApiResponse.success(
                                chatService
                                                .getAnnouncement(
                                                                userDetails.getUser().getId(),
                                                                coupleId)
                                                .orElse(null));
        }

        @DeleteMapping("/{coupleId}/announcement")
        public ApiResponse<Void> removeAnnouncement(
                        @PathVariable Long coupleId,

                        @AuthenticationPrincipal CustomUserDetails userDetails) {
                chatService.removeAnnouncement(
                                userDetails.getUser().getId(),
                                coupleId);

                return ApiResponse.success(
                                null);
        }

        // elasticsearch용 임시 API
        @PostMapping("/search/reindex")
        public ApiResponse<Void> reindexMessages() {

                chatSearchService.reindexAllMessages();

                return ApiResponse.success(null);
        }
}
