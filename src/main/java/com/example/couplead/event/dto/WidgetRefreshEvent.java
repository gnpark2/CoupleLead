package com.example.couplead.event.dto;

public record WidgetRefreshEvent(
    Long coupleId,
    String reason
) {
    
}
