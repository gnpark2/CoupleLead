package com.example.couplead.anniversary.dto.request;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public record UpdateHomeAnniversarySelectionRequest(

        @NotNull List<Long> anniversaryIds

) {
}