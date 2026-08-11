package com.example.couplead.weather.dto.response;

import java.util.List;

public record OpenWeatherResponse(
    Main main,
    List<Weather> weather
) {
    public record Main(Double temp) {}

    public record Weather(
        String main,
        String description,
        String icon
    ) {}
}
