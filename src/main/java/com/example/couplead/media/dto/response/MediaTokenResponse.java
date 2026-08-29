package com.example.couplead.media.dto.response;

public record MediaTokenResponse(
        String url,
        String token,
        String roomName) {
}