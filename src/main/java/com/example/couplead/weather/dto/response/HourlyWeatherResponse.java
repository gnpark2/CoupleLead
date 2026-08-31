package com.example.couplead.weather.dto.response;

import java.util.List;

public record HourlyWeatherResponse(
        List<HourlyWeatherItem> items) {

    public record HourlyWeatherItem(
            String time,
            Double temperature,
            Integer weatherCode) {
    }
}