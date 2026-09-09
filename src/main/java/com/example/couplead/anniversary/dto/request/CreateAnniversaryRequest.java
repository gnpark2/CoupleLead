package com.example.couplead.anniversary.dto.request;

import java.time.LocalDate;

import com.example.couplead.anniversary.domain.AnniversaryType;
import com.example.couplead.anniversary.domain.RepeatType;

public record CreateAnniversaryRequest(

        String title,

        LocalDate anniversaryDate,

        AnniversaryType type,

        RepeatType repeatType,

        String customTypeName

) {
}