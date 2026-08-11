package com.example.couplead.weather.service;

import com.example.couplead.weather.dto.response.WeatherResponse;

public interface WeatherService {
    WeatherResponse getCurrentWeather(
        Double latitude,
        Double longitude
    );
}