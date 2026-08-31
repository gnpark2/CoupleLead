package com.example.couplead.chat.service;

import java.time.Duration;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatUnreadCacheService {

    private static final String PREFIX = "chat:unread:";

    private static final Duration TTL = Duration.ofDays(30);

    private final StringRedisTemplate redisTemplate;

    public void set(
            Long userId,
            Long coupleId,
            long unreadCount) {
        redisTemplate.opsForValue().set(
                createKey(
                        userId,
                        coupleId),
                String.valueOf(unreadCount),
                TTL);
    }

    public long get(
            Long userId,
            Long coupleId) {
        String value = redisTemplate.opsForValue().get(
                createKey(
                        userId,
                        coupleId));

        if (value == null) {
            return -1;
        }

        try {
            return Long.parseLong(
                    value);
        } catch (NumberFormatException e) {
            redisTemplate.delete(
                    createKey(
                            userId,
                            coupleId));

            return -1;
        }
    }

    public void clear(
            Long userId,
            Long coupleId) {
        redisTemplate.delete(
                createKey(
                        userId,
                        coupleId));
    }

    private String createKey(
            Long userId,
            Long coupleId) {
        return PREFIX
                + userId
                + ":"
                + coupleId;
    }
}