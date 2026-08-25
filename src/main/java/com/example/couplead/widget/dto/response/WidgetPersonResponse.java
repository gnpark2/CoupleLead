package com.example.couplead.widget.dto.response;

public record WidgetPersonResponse(
    Long id,
    String nickname,
    String city,
    String timezone,
    String localTime,
    Double temperature,
    String weatherCondition,
    String weatherIcon
) {
    
}
