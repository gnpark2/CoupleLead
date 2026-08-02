package com.example.couplead.auth.service;

import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.example.couplead.auth.security.JwtProperties;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {
    private static final String PREFIX = "refresh:";
    private final RedisTemplate<String, String> redisTemplate;
    private final JwtProperties jwtProperties;

    @Override
    public void save(Long userId, String refreshToken) {
        redisTemplate.opsForValue().set(
            PREFIX + userId,
            refreshToken,
            jwtProperties.getRefreshTokenExpiration(),
            TimeUnit.MILLISECONDS
        );
    }

    @Override
    public String find(Long userId) {
        return redisTemplate.opsForValue()
            .get(PREFIX + userId);
    }

    @Override
    public void delete(Long userId) {
        redisTemplate.delete(PREFIX + userId);
    }

    @Override
    public boolean validate(Long userId, String refreshToken) {
        String saved = find(userId);

        return saved != null && saved.equals(refreshToken);
    }
}
