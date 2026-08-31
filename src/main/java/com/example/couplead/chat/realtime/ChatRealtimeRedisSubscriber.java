package com.example.couplead.chat.realtime;

import java.nio.charset.StandardCharsets;

import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatRealtimeRedisSubscriber
        implements MessageListener {

    private final ObjectMapper objectMapper;

    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public void onMessage(
            Message message,
            byte[] pattern) {

        try {
            String json = new String(
                    message.getBody(),
                    StandardCharsets.UTF_8);

            ChatRealtimeEvent event = objectMapper.readValue(
                    json,
                    ChatRealtimeEvent.class);

            String destination = resolveDestination(
                    event);

            messagingTemplate.convertAndSend(
                    destination,
                    event.payload());

            log.debug(
                    "[CHAT REDIS DELIVER] type={}, coupleId={}, destination={}",
                    event.type(),
                    event.coupleId(),
                    destination);

        } catch (Exception e) {
            log.error(
                    "채팅 Redis 이벤트 처리 실패",
                    e);
        }
    }

    private String resolveDestination(
            ChatRealtimeEvent event) {

        return switch (event.type()) {

            case MESSAGE ->
                "/topic/chat/"
                        + event.coupleId();

            case READ ->
                "/topic/chat/read/"
                        + event.coupleId();

            case TYPING ->
                "/topic/chat/typing/"
                        + event.coupleId();

            case EDIT ->
                "/topic/chat/edit/"
                        + event.coupleId();

            case DELETE ->
                "/topic/chat/delete/"
                        + event.coupleId();

            case ANNOUNCEMENT ->
                "/topic/chat/announcement/"
                        + event.coupleId();
        };
    }
}