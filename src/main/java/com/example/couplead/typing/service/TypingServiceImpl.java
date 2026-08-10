package com.example.couplead.typing.service;

import java.time.Duration;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.example.couplead.typing.dto.ChatTypingEvent;
import com.example.couplead.event.producer.ChatTypingProducer;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TypingServiceImpl implements TypingService {
    private static final String PREFIX = "typing:";

    private final StringRedisTemplate redisTemplate;
    private final ChatTypingProducer chatTypingProducer;

    @Override
    public void typing(
        Long coupleId,
        Long userId,
        String nickname
    ) {
        redisTemplate.opsForValue().set(
            PREFIX + userId,
            "true",
            Duration.ofSeconds(3000)
        );

        chatTypingProducer.publish(
            new ChatTypingEvent(userId, coupleId, nickname, true)
        );
    }

    @Override
    public boolean isTyping(Long userId) {
        return redisTemplate.hasKey(PREFIX + userId);
    }
}
