package com.example.couplead.event.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.example.couplead.chat.domain.Message;
import com.example.couplead.chat.dto.response.ChatMessageResponse;
import com.example.couplead.chat.repository.MessageRepository;
import com.example.couplead.chat.service.ChatCacheService;
import com.example.couplead.couple.domain.Couple;
import com.example.couplead.couple.repository.CoupleRepository;
import com.example.couplead.user.domain.User;
import com.example.couplead.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatMessageConsumer {
    private final ChatCacheService chatCacheService;
    private final SimpMessagingTemplate messagingTemplate;
    private final MessageRepository messageRepository;
    private final CoupleRepository coupleRepository;
    private final UserRepository userRepository;

    @KafkaListener(topics = "chat-message", groupId = "chat-group-v3")
    @Transactional
    public void consume(ChatMessageResponse message) {
        
        Couple couple = coupleRepository.findById(message.coupleId()).orElseThrow();
        User sender = userRepository.findById(message.senderId()).orElseThrow();

        messageRepository.save(
            Message.builder()
                .couple(couple)
                .sender(sender)
                .content(message.content())
                .sentAt(message.sentAt())
                .build()
        );
        
        chatCacheService.save(message);
        
        messagingTemplate.convertAndSend("/topic/chat/" + message.coupleId(), message);
    }
}
