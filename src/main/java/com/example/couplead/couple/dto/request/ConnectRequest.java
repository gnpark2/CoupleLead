package com.example.couplead.couple.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ConnectRequest(
    @NotBlank
    String inviteCode
) {
    
}
