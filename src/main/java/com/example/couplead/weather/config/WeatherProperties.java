package com.example.couplead.weather.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "weather.openweather")
public record WeatherProperties (
    String baseUrl,
    String apiKey
) {

}
