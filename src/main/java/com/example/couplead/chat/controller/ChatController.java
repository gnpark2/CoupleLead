package com.example.couplead.chat.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.couplead.auth.security.CustomUserDetails;
import com.example.couplead.chat.dto.request.ChatMessageRequest;
import com.example.couplead.chat.dto.response.ChatHistoryResponse;
import com.example.couplead.chat.dto.response.ChatMessageResponse;
import com.example.couplead.chat.service.ChatService;
import com.example.couplead.common.response.ApiResponse;
import com.example.couplead.event.producer.ChatEventProducer;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat")
public class ChatController {
    private final ChatEventProducer chatEventProducer;
    private final ChatService chatService;

    @MessageMapping("/chat/send")
    public void send(ChatMessageRequest request, @AuthenticationPrincipal CustomUserDetails userDetails) {
        // ChatMessageResponse message = new ChatMessageResponse(
        //     request.coupleId(),
        //     userDetails.getUser().getId(),
        //     userDetails.getUser().getNickname(),
        //     request.content(),
        //     LocalDateTime.now()
        // );

        // jwt인증 없이 websocket test를 위한 임시 코드
        ChatMessageResponse message =
        new ChatMessageResponse(
                request.coupleId(),
                1L,
                "Tester",
                request.content(),
                LocalDateTime.now()
        );
        //
        chatEventProducer.publish(message);
    }

    @GetMapping("/{coupleId}")
    public ApiResponse<List<ChatHistoryResponse>> getMessages(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable Long coupleId) {
        return ApiResponse.success(
            chatService.getMessages(
                userDetails.getUser().getId(),
                coupleId
            )
        );
    }
    
}
