package com.example.couplead.weather.service;

import com.example.couplead.weather.dto.response.HourlyWeatherResponse;
import com.example.couplead.weather.dto.response.WeatherResponse;

public interface WeatherService {
    WeatherResponse getCurrentWeather(
            Double latitude,
            Double longitude);

    HourlyWeatherResponse getHourlyForecast(
            Double latitude,
            Double longitude,
            String timezone);

    HourlyWeatherResponse getPartnerHourlyForecast(
            Long userId);
}