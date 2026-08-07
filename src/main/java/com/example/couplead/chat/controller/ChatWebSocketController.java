package com.example.couplead.chat.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.time.LocalDateTime;

import org.springframework.messaging.handler.annotation.MessageMapping;

import com.example.couplead.chat.dto.request.ChatMessageRequest;
import com.example.couplead.chat.dto.response.ChatMessageResponse;
import com.example.couplead.event.producer.ChatEventProducer;
import com.example.couplead.user.domain.User;
import com.example.couplead.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/chat")
public class ChatWebSocketController {

    private final ChatEventProducer chatEventProducer;
    private final UserRepository userRepository;

    @MessageMapping("/chat/send")
    public void send(
            ChatMessageRequest request,
            Principal principal
    ) {

        if (principal == null) {
            throw new IllegalStateException("Principal is null");
        }

        Long userId = Long.parseLong(principal.getName());

        User user = userRepository.findById(userId)
                .orElseThrow();

        ChatMessageResponse message =
                new ChatMessageResponse(
                        request.coupleId(),
                        user.getId(),
                        user.getNickname(),
                        request.content(),
                        LocalDateTime.now()
                );

        chatEventProducer.publish(message);
    }
}