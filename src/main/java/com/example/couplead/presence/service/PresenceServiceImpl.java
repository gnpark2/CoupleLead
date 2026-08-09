package com.example.couplead.presence.service;

import java.time.LocalDateTime;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.example.couplead.presence.dto.UserPresenceEvent;
import com.example.couplead.event.producer.PresenceEventProducer;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PresenceServiceImpl implements PresenceService {
    private static final String ONLINE_PREFIX = "online:";
    private static final String LAST_SEEN_PREFIX = "lastSeen:";

    private final StringRedisTemplate redisTemplate;
    private final PresenceEventProducer presenceEventProducer;

    @Override
    public void connect(Long userId) {
        redisTemplate.opsForValue().set(ONLINE_PREFIX + userId, "true");

        presenceEventProducer.publish(new UserPresenceEvent(userId, true, LocalDateTime.now()));
    }

    @Override
    public void disconnect(Long userId) {
        redisTemplate.delete(ONLINE_PREFIX + userId);

        String now = LocalDateTime.now().toString();

        redisTemplate.opsForValue().set(LAST_SEEN_PREFIX + userId, now);

        presenceEventProducer.publish(new UserPresenceEvent(userId, false, LocalDateTime.now()));
    }

    @Override
    public boolean isOnline(Long userId) {
        return redisTemplate.hasKey(ONLINE_PREFIX + userId);
    }

    @Override
    public String getLastSeen(Long userId) {
        return redisTemplate.opsForValue().get(LAST_SEEN_PREFIX + userId);
    }
}
