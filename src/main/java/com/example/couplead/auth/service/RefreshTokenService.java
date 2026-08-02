package com.example.couplead.auth.service;

public interface RefreshTokenService {
    void save(Long userId, String refreshToken);
    String find(Long userId);
    void delete(Long userId);
    boolean validate(Long userId, String refreshToken);
}
