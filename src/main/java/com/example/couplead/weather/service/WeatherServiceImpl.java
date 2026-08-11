package com.example.couplead.weather.service;

import java.time.Duration;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.example.couplead.weather.config.WeatherProperties;
import com.example.couplead.weather.dto.response.OpenWeatherResponse;
import com.example.couplead.weather.dto.response.WeatherResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WeatherServiceImpl implements WeatherService {
    private static final String PREFIX = "weather:";
    private static final Duration CACHE_TTL = Duration.ofMinutes(30);

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;
    private final WeatherProperties weatherProperties;

    @Override
    public WeatherResponse getCurrentWeather(
        Double latitude,
        Double longitude
    ) {
        if (latitude == null || longitude == null) {
            return null;
        }

        String key = createKey(latitude, longitude);
        WeatherResponse cached = getCache(key);

        if (cached != null) {
            return cached;
        }

        WeatherResponse weather = requestWeather(latitude, longitude);
        
        saveCache(key, weather);
        
        return weather;
    }

    private WeatherResponse requestWeather(
        Double latitude,
        Double longitude
    ) {
        RestClient restClient = RestClient.builder()
            .baseUrl(weatherProperties.baseUrl())
            .build();

        OpenWeatherResponse response = restClient
            .get()
            .uri(uriBuilder -> uriBuilder
                .path("/data/2.5/weather")
                .queryParam("lat", latitude)
                .queryParam("lon", longitude)
                .queryParam("appid", weatherProperties.apiKey())
                .queryParam("units", "metric")
                .queryParam("lang", "kr")
                .build()
            )
            .retrieve()
            .body(OpenWeatherResponse.class);

        if(response == null || response.main() == null || response.weather() == null || response.weather().isEmpty()) {
            throw new IllegalStateException("날씨 정보를 가져오지 못했습니다.");
        }

        OpenWeatherResponse.Weather weather = response.weather().getFirst();

        return new WeatherResponse(
            response.main().temp(),
            weather.main(),
            weather.description(),
            weather.icon()
        );
    }

    private WeatherResponse getCache(String key) {
        String json = redisTemplate.opsForValue().get(key);

        if (json == null || json.isBlank()) {
            return null;
        }
        try {
            return objectMapper.readValue(json, WeatherResponse.class);
        } catch (JsonProcessingException e) {
            redisTemplate.delete(key);
            return null;
        }
    }

    private void saveCache(
        String key,
        WeatherResponse weather
    ) {
        try {
            String json = objectMapper.writeValueAsString(weather);

            redisTemplate.opsForValue().set(key, json, CACHE_TTL);
        } catch (Exception e) {
            throw new IllegalStateException("날씨 캐시 직렬화 실패", e);
        }
    }

    private String createKey(
        Double latitude,
        Double longitude
    ) {
        return PREFIX + String.format("%.4f", latitude) + ":" + String.format("%.4f", longitude);
    }
}
