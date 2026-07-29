package com.example.couplead.auth.dto.response;

public record LoginResponse(
    String accessToken,
    String refreshToken
) {
    
}
