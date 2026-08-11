package com.example.couplead.widget.dto.request;

import jakarta.validation.constraints.NotNull;

public record SelectWidgetAnniversaryRequest(
    @NotNull
    Long anniversaryId
) {
    
}
