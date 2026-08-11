package com.example.couplead.anniversary.dto.request;

import java.time.LocalDate;

import com.example.couplead.anniversary.domain.AnniversaryType;
import com.example.couplead.anniversary.domain.RepeatType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateAnniversaryRequest(
    @NotBlank
    String title,

    @NotNull
    LocalDate anniversaryDate,

    @NotNull
    AnniversaryType type,

    @NotNull
    RepeatType repeatType
) {
    
}
