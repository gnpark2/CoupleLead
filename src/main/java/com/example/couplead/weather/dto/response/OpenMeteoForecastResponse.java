package com.example.couplead.weather.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record OpenMeteoForecastResponse(
        String timezone,
        Hourly hourly) {

    public record Hourly(
            List<String> time,

            @JsonProperty("temperature_2m") List<Double> temperature,

            @JsonProperty("weather_code") List<Integer> weatherCode) {
    }
}