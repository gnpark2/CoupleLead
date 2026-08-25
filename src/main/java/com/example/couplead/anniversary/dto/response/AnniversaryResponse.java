package com.example.couplead.anniversary.dto.response;

import java.time.LocalDate;

import com.example.couplead.anniversary.domain.Anniversary;

public record AnniversaryResponse(
        Long id,
        String title,
        LocalDate anniversaryDate,
        String type,
        String repeatType,
        String customTypeName) {
    public static AnniversaryResponse from(
            Anniversary anniversary) {
        return new AnniversaryResponse(
                anniversary.getId(),
                anniversary.getTitle(),
                anniversary.getAnniversaryDate(),
                anniversary.getType().name(),
                anniversary.getRepeatType().name(),
                anniversary.getCustomTypeName());
    }
}
