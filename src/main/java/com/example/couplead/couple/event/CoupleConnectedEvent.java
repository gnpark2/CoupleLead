package com.example.couplead.couple.event;

public record CoupleConnectedEvent(
        Long coupleId,
        Long userAId,
        Long userBId) {
}