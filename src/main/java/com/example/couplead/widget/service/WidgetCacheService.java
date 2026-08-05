package com.example.couplead.widget.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.example.couplead.widget.dto.response.CoupleWidgetResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WidgetCacheService {
    private static final String PREFIX = "widget:couple:";
    private final RedisTemplate<String, Object> redisTemplate;

    public void updateCache(
        Long coupleId,
        LocalDate anniversary
    ) {
        long daysTogether = ChronoUnit.DAYS.between(anniversary, LocalDate.now());
        LocalDate nextAnniversary = anniversary.withYear(LocalDate.now().getYear());

        if (nextAnniversary.isBefore(LocalDate.now())) {
            nextAnniversary = nextAnniversary.plusYears(1);
        }

        long next = ChronoUnit.DAYS.between(LocalDate.now(), nextAnniversary);
        redisTemplate.opsForHash().putAll(PREFIX + coupleId, Map.of(
            "daysTogether", String.valueOf(daysTogether),
            "nextAnniversary", String.valueOf(next),
            "updatedAt", LocalDateTime.now().toString()
        ));
    }

    public CoupleWidgetResponse getCache(Long coupleId) {
        Map<Object, Object> data = redisTemplate.opsForHash().entries(PREFIX + coupleId);
        if (data.isEmpty()) {
            return null;
        }

        return new CoupleWidgetResponse(
            Long.parseLong(
                data.get("daysTogether").toString()
            ),
            Long.parseLong(
                data.get("nextAnniversary").toString()
            ),
            data.get("updatedAt").toString()
        );
    }
}
