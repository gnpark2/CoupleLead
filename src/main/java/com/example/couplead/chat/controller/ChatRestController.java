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

import com.example.couplead.chat.dto.response.ChatHistoryPageResponse;
import com.example.couplead.auth.security.CustomUserDetails;
import com.example.couplead.chat.document.MessageDocument;
import com.example.couplead.chat.dto.response.ChatImageUploadResponse;
import com.example.couplead.chat.service.ChatService;
import com.example.couplead.common.response.ApiResponse;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

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
    public ApiResponse<List<MessageDocument>> search(@PathVariable Long coupleId, @RequestParam String keyword) {
        return ApiResponse.success(chatSearchService.search(coupleId, keyword));
    }

    @PostMapping(value = "/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<ChatImageUploadResponse> uploadImage(
            @RequestPart("file") MultipartFile file) {
        String imageUrl = chatImageStorageService.save(file);

        return ApiResponse.success(
                new ChatImageUploadResponse(
                        imageUrl));
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
}
