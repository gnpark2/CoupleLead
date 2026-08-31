package com.example.couplead.weather.service;

import java.time.Duration;
import java.util.List;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.example.couplead.couple.domain.CoupleMember;
import com.example.couplead.couple.repository.CoupleMemberRepository;
import com.example.couplead.user.domain.User;
import com.example.couplead.user.repository.UserRepository;
import com.example.couplead.weather.config.WeatherProperties;
import com.example.couplead.weather.dto.response.HourlyWeatherResponse;
import com.example.couplead.weather.dto.response.OpenMeteoForecastResponse;
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
    private static final String OPEN_METEO_URL = "https://api.open-meteo.com/v1/forecast";

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;
    private final WeatherProperties weatherProperties;
    private final UserRepository userRepository;
    private final CoupleMemberRepository coupleMemberRepository;

    @Override
    public WeatherResponse getCurrentWeather(
            Double latitude,
            Double longitude) {
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
            Double longitude) {
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
                        .build())
                .retrieve()
                .body(OpenWeatherResponse.class);

        if (response == null || response.main() == null || response.weather() == null || response.weather().isEmpty()) {
            throw new IllegalStateException("날씨 정보를 가져오지 못했습니다.");
        }

        OpenWeatherResponse.Weather weather = response.weather().getFirst();

        return new WeatherResponse(
                response.main().temp(),
                weather.main(),
                weather.description(),
                weather.icon());
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
            WeatherResponse weather) {
        try {
            String json = objectMapper.writeValueAsString(weather);

            redisTemplate.opsForValue().set(key, json, CACHE_TTL);
        } catch (Exception e) {
            throw new IllegalStateException("날씨 캐시 직렬화 실패", e);
        }
    }

    private String createKey(
            Double latitude,
            Double longitude) {
        return PREFIX + String.format("%.4f", latitude) + ":" + String.format("%.4f", longitude);
    }

    @Override
    public HourlyWeatherResponse getHourlyForecast(
            Double latitude,
            Double longitude,
            String timezone) {

        if (latitude == null ||
                longitude == null ||
                timezone == null ||
                timezone.isBlank()) {

            return new HourlyWeatherResponse(
                    List.of());
        }

        OpenMeteoForecastResponse response = RestClient.create()
                .get()
                .uri(uriBuilder -> uriBuilder
                        .scheme("https")
                        .host("api.open-meteo.com")
                        .path("/v1/forecast")
                        .queryParam(
                                "latitude",
                                latitude)
                        .queryParam(
                                "longitude",
                                longitude)
                        .queryParam(
                                "hourly",
                                "temperature_2m,weather_code")
                        .queryParam(
                                "timezone",
                                timezone)
                        .queryParam(
                                "forecast_hours",
                                48)
                        .build())
                .retrieve()
                .body(
                        OpenMeteoForecastResponse.class);

        if (response == null ||
                response.hourly() == null) {

            return new HourlyWeatherResponse(
                    List.of());
        }

        List<String> times = response.hourly().time();

        List<Double> temperatures = response.hourly().temperature();

        List<Integer> weatherCodes = response.hourly().weatherCode();

        int size = Math.min(
                48,
                Math.min(
                        times.size(),
                        Math.min(
                                temperatures.size(),
                                weatherCodes.size())));

        List<HourlyWeatherResponse.HourlyWeatherItem> result = new java.util.ArrayList<>();

        for (int i = 0; i < size; i++) {
            result.add(
                    new HourlyWeatherResponse.HourlyWeatherItem(
                            times.get(i),
                            temperatures.get(i),
                            weatherCodes.get(i)));
        }

        return new HourlyWeatherResponse(
                result);
    }

    @Override
    public HourlyWeatherResponse getPartnerHourlyForecast(
            Long userId) {

        User me = userRepository
                .findById(userId)
                .orElseThrow(
                        () -> new IllegalArgumentException(
                                "사용자를 찾을 수 없습니다."));

        CoupleMember myMember = coupleMemberRepository
                .findByUser(me)
                .orElseThrow(
                        () -> new IllegalArgumentException(
                                "커플 정보를 찾을 수 없습니다."));

        List<CoupleMember> members = coupleMemberRepository
                .findByCoupleWithUser(
                        myMember.getCouple());

        User partner = members.stream()
                .map(CoupleMember::getUser)
                .filter(user -> !user.getId()
                        .equals(userId))
                .findFirst()
                .orElseThrow(
                        () -> new IllegalArgumentException(
                                "상대방 정보를 찾을 수 없습니다."));

        /*
         * 상대방이 아직 지역 설정을 하지 않은 경우
         */
        if (partner.getLatitude() == null ||
                partner.getLongitude() == null ||
                partner.getTimezone() == null ||
                partner.getTimezone().isBlank()) {

            return new HourlyWeatherResponse(
                    List.of());
        }

        return getHourlyForecast(
                partner.getLatitude(),
                partner.getLongitude(),
                partner.getTimezone());
    }
}
