package com.example.couplead.chat.controller;

import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.time.Instant;

import org.springframework.messaging.handler.annotation.MessageMapping;

import com.example.couplead.chat.domain.Message;
import com.example.couplead.chat.dto.request.ChatMessageRequest;
import com.example.couplead.chat.dto.request.ChatTypingRequest;
import com.example.couplead.chat.dto.response.ChatMessageResponse;
import com.example.couplead.chat.repository.MessageRepository;
import com.example.couplead.common.exception.CustomException;
import com.example.couplead.common.exception.ErrorCode;
import com.example.couplead.event.producer.ChatEventProducer;
import com.example.couplead.typing.service.TypingService;
import com.example.couplead.user.domain.User;
import com.example.couplead.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/api/chat")
public class ChatWebSocketController {

        private final ChatEventProducer chatEventProducer;
        private final UserRepository userRepository;
        private final TypingService typingService;
        private final MessageRepository messageRepository;

        @MessageMapping("/chat/send")
        @Transactional
        public void send(
                        ChatMessageRequest request,
                        Principal principal) {

                if (principal == null) {
                        throw new IllegalStateException("Principal is null");
                }

                Long userId = Long.parseLong(principal.getName());

                User user = userRepository.findById(userId)
                                .orElseThrow();

                Message replyToMessage = null;

                if (request.replyToMessageId() != null) {
                        replyToMessage = messageRepository
                                        .findById(
                                                        request.replyToMessageId())
                                        .orElseThrow(
                                                        () -> new CustomException(
                                                                        ErrorCode.MESSAGE_NOT_FOUND));

                        if (!replyToMessage
                                        .getCouple()
                                        .getId()
                                        .equals(
                                                        request.coupleId())) {

                                throw new CustomException(
                                                ErrorCode.MESSAGE_NOT_FOUND);
                        }
                }

                log.info(
                                "[CHAT SEND] clientMessageId={}",
                                request.clientMessageId());

                ChatMessageResponse message = new ChatMessageResponse(
                                request.coupleId(),
                                user.getId(),
                                user.getNickname(),
                                request.type(),
                                request.content(),
                                Instant.now(),
                                false,
                                null,
                                replyToMessage == null
                                                ? null
                                                : replyToMessage.getId(),

                                replyToMessage == null
                                                ? null
                                                : replyToMessage
                                                                .getSender()
                                                                .getNickname(),

                                replyToMessage == null
                                                ? null
                                                : replyToMessage
                                                                .getType(),

                                replyToMessage == null
                                                ? null
                                                : replyToMessage
                                                                .isDeleted()
                                                                                ? "삭제된 메시지입니다."
                                                                                : replyToMessage
                                                                                                .getContent(),
                                request.clientMessageId());

                chatEventProducer.publish(message);
        }

        @MessageMapping("/chat/typing")
        public void typing(
                        ChatTypingRequest request,
                        Principal principal) {
                Long userId = Long.parseLong(principal.getName());

                User user = userRepository.findById(userId).orElseThrow();
                log.info("Typing: {}", user.getNickname());
                typingService.typing(request.coupleId(), userId, user.getNickname());
        }
}