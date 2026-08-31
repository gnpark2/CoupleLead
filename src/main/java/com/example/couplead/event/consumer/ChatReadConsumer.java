package com.example.couplead.event.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.example.couplead.chat.realtime.ChatRealtimeEventType;
import com.example.couplead.chat.realtime.ChatRealtimeRedisPublisher;
import com.example.couplead.event.dto.ChatReadEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatReadConsumer {

    private final ChatRealtimeRedisPublisher chatRealtimeRedisPublisher;

    @KafkaListener(topics = "chat-read", groupId = "chat-read-group")
    public void consume(
            ChatReadEvent event) {

        log.info(
                "읽음 이벤트 수신: {}",
                event);

        chatRealtimeRedisPublisher.publish(
                ChatRealtimeEventType.READ,
                event.coupleId(),
                event);
    }
}