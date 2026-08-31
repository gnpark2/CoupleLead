package com.example.couplead.chat.realtime;

import java.util.UUID;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatRealtimeRedisPublisher {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    public void publish(
            ChatRealtimeEventType type,
            Long coupleId,
            Object payload) {

        JsonNode payloadNode = objectMapper.valueToTree(
                payload);

        ChatRealtimeEvent event = new ChatRealtimeEvent(
                UUID.randomUUID()
                        .toString(),
                type,
                coupleId,
                payloadNode);

        try {
            String json = objectMapper.writeValueAsString(
                    event);

            redisTemplate.convertAndSend(
                    ChatRealtimeChannel.CHANNEL,
                    json);

            log.debug(
                    "[CHAT REDIS PUBLISH] type={}, coupleId={}, eventId={}",
                    type,
                    coupleId,
                    event.eventId());

        } catch (Exception e) {
            throw new IllegalStateException(
                    "채팅 실시간 Redis 이벤트 발행 실패",
                    e);
        }
    }
}