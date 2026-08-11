package com.example.couplead.weather.dto.response;

public record WeatherResponse(
    Double temperature,
    String condition,
    String description,
    String icon
) {
    
}
