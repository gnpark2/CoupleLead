package com.example.couplead.auth.dto.request;

import jakarta.validation.constraints.*;

public record SignupRequest(
    @Email
    @NotBlank
    String email,

    @NotBlank
    @Size(min = 8, max = 20)
    String password,

    @NotBlank
    @Size(min =2, max = 15)
    String nickname
) {
}
