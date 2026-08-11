package com.example.couplead.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateLocationRequest(
    @NotBlank
    String country,

    @NotBlank
    String city,

    @NotBlank
    String timezone,

    @NotNull
    Double latitude,

    @NotNull
    Double longitude
) {
    
}
