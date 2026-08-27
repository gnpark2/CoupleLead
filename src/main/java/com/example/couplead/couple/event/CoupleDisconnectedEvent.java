package com.example.couplead.couple.event;

public record CoupleDisconnectedEvent(
        Long coupleId,
        Long userAId,
        Long userBId) {
}