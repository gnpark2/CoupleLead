package com.example.couplead.chat.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.example.couplead.chat.dto.response.ChatMessageResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatCacheService {
    private static final String PREFIX = "chat:";
    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    public void save(ChatMessageResponse message) {

        try {
            String json = objectMapper.writeValueAsString(message);
            String key = PREFIX + message.coupleId();

            redisTemplate.opsForList().rightPush(key, json);
            redisTemplate.opsForList().trim(key, -50, -1);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void invalidate(
            Long coupleId) {
        String key = PREFIX + coupleId;

        redisTemplate.delete(
                key);
    }
}
