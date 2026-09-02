package com.example.couplead.domain.service;

public interface FirebasePushService {

    void sendToUser(
            Long userId,
            String title,
            String body,
            Long coupleId,
            Long senderId);
}