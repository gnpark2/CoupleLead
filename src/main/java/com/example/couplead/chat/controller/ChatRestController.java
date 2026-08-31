package com.example.couplead.chat.controller;

import com.example.couplead.chat.service.ChatImageStorageService;
import com.example.couplead.chat.service.ChatSearchService;

import java.time.Instant;
import java.time.LocalDate;
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
import com.example.couplead.chat.domain.MessageType;
import com.example.couplead.chat.dto.response.ChatImageUploadResponse;
import com.example.couplead.chat.dto.response.ChatImagesUploadResponse;
import com.example.couplead.chat.dto.response.ChatSearchPageResponse;
import com.example.couplead.chat.dto.response.ChatUnreadBoundaryResponse;
import com.example.couplead.chat.service.ChatService;
import com.example.couplead.common.exception.CustomException;
import com.example.couplead.common.exception.ErrorCode;
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
        public ApiResponse<ChatSearchPageResponse> searchMessages(
                        @PathVariable Long coupleId,
                        @RequestParam(defaultValue = "") String keyword,
                        @RequestParam(defaultValue = "true") boolean useNori,
                        @RequestParam(defaultValue = "20") int size,
                        @RequestParam(required = false) Instant beforeSentAt,
                        @RequestParam(required = false) Long beforeMessageId,

                        @RequestParam(required = false) Long senderId,

                        @RequestParam(required = false) MessageType type,
                        @RequestParam(required = false) LocalDate fromDate,

                        @RequestParam(required = false) LocalDate toDate,

                        @AuthenticationPrincipal CustomUserDetails userDetails) {
                return ApiResponse.success(
                                chatService.searchMessages(
                                                userDetails.getUser().getId(),
                                                coupleId,
                                                keyword,
                                                useNori,
                                                size,
                                                beforeSentAt,
                                                beforeMessageId,
                                                senderId,
                                                type,
                                                fromDate,
                                                toDate));
        }

        @PostMapping(value = "/{coupleId}/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
        public ApiResponse<ChatImagesUploadResponse> uploadChatImages(
                        @PathVariable Long coupleId,
                        @RequestPart("files") List<MultipartFile> files,
                        @AuthenticationPrincipal CustomUserDetails userDetails) {

                if (files == null || files.isEmpty()) {
                        throw new CustomException(
                                        ErrorCode.INVALID_REQUEST);
                }

                if (files.size() > 10) {
                        throw new CustomException(
                                        ErrorCode.INVALID_REQUEST);
                }

                /*
                 * 기존에 couple 접근 권한 검증 메서드가 있다면
                 * 반드시 여기서 호출
                 */
                chatService.validateCoupleMember(
                                userDetails.getUser().getId(),
                                coupleId);

                List<String> urls = chatImageStorageService.saveAll(
                                files);

                List<ChatImageUploadResponse> images = urls.stream()
                                .map(ChatImageUploadResponse::new)
                                .toList();

                return ApiResponse.success(
                                new ChatImagesUploadResponse(
                                                images));
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

        @GetMapping("/{coupleId}/unread-boundary")
        public ApiResponse<ChatUnreadBoundaryResponse> getUnreadBoundary(
                        @PathVariable Long coupleId,
                        @AuthenticationPrincipal CustomUserDetails userDetails) {
                return ApiResponse.success(
                                chatService.getUnreadBoundary(
                                                userDetails.getUser().getId(),
                                                coupleId));
        }

        // elasticsearch용 임시 API
        @PostMapping("/search/reindex")
        public ApiResponse<Void> reindexMessages() {

                chatSearchService.reindexAllMessages();

                return ApiResponse.success(null);
        }
}
