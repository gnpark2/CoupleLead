package com.example.couplead.event.consumer;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.example.couplead.chat.document.MessageDocument;
import com.example.couplead.chat.domain.Message;
import com.example.couplead.chat.domain.MessageType;
import com.example.couplead.chat.dto.response.ChatMessageResponse;
import com.example.couplead.chat.repository.MessageRepository;
import com.example.couplead.chat.repository.MessageSearchRepository;
import com.example.couplead.chat.service.ChatCacheService;
import com.example.couplead.couple.domain.Couple;
import com.example.couplead.couple.repository.CoupleRepository;
import com.example.couplead.user.domain.User;
import com.example.couplead.user.repository.UserRepository;
import com.example.couplead.chat.event.ChatMessageCommittedEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatMessageConsumer {
    private final ChatCacheService chatCacheService;
    private final MessageRepository messageRepository;
    private final CoupleRepository coupleRepository;
    private final UserRepository userRepository;
    private final MessageSearchRepository messageSearchRepository;
    private final ApplicationEventPublisher eventPublisher;

    @KafkaListener(topics = "chat-message", groupId = "chat-group-v3")
    @Transactional
    public void consume(ChatMessageResponse message) {

        Couple couple = coupleRepository.findById(message.coupleId()).orElseThrow();
        User sender = userRepository.findById(message.senderId()).orElseThrow();

        // mysql 저장
        Message savedMessage = messageRepository.save(
                Message.builder()
                        .couple(couple)
                        .sender(sender)
                        .type(
                                message.type() == null
                                        ? MessageType.TEXT
                                        : message.type()
                        )
                        .content(message.content())
                        .sentAt(message.sentAt())
                        .build()
);

        // Elasticsearch 색인
        messageSearchRepository.save(
                MessageDocument.builder()
                        .id(savedMessage.getId().toString())
                        .coupleId(couple.getId())
                        .senderId(sender.getId())
                        .senderNickname(sender.getNickname())
                        .content(savedMessage.getContent())
                        .sentAt(savedMessage.getSentAt())
                        .build());

        // Redis 최근 채팅 캐시
        chatCacheService.save(message);

        // Websocket 실시간 전송
        // messagingTemplate.convertAndSend("/topic/chat/" + message.coupleId(),
        // message);

        // widget 캐시 무효화
        // widgetRefreshProducer.publish(
        // couple.getId(),
        // "CHAT_MESSAGE"
        // );

        eventPublisher.publishEvent(
                new ChatMessageCommittedEvent(
                        couple.getId(),
                        message));
    }
}
