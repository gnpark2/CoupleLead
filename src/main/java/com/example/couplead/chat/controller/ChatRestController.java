package com.example.couplead.chat.controller;

import com.example.couplead.chat.service.ChatSearchService;
import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.couplead.auth.security.CustomUserDetails;
import com.example.couplead.chat.document.MessageDocument;
import com.example.couplead.chat.dto.response.ChatHistoryResponse;
import com.example.couplead.chat.service.ChatService;
import com.example.couplead.common.response.ApiResponse;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat")
public class ChatRestController {
    private final ChatSearchService chatSearchService;
    private final ChatService chatService;

    @GetMapping("/{coupleId}")
    public ApiResponse<List<ChatHistoryResponse>> getMessages(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable Long coupleId) {
        return ApiResponse.success(
            chatService.getMessages(
                userDetails.getUser().getId(),
                coupleId
            )
        );
    }

    @PostMapping("/{coupleId}/read")
    public ApiResponse<Void> markAsRead(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable Long coupleId) {
        chatService.markAsRead(userDetails.getUser().getId(), coupleId);
    
        return ApiResponse.success(null);
    }

    @GetMapping("/{coupleId}/search")
    public ApiResponse<List<MessageDocument>> search(@PathVariable Long coupleId, @RequestParam String keyword) {
        return ApiResponse.success(chatSearchService.search(coupleId, keyword));
    }
}
