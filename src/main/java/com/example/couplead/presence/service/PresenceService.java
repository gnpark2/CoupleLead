package com.example.couplead.presence.service;

public interface PresenceService {
    void connect(Long userId);
    void disconnect(Long userId);
    boolean isOnline(Long userId);
    String getLastSeen(Long userId);
}
