package com.example.couplead.media.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record MediaCallActionRequest(

        @NotBlank String callId,

        @NotNull Long callerUserId) {
}