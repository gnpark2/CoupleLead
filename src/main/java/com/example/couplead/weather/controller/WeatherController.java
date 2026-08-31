package com.example.couplead.weather.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.couplead.auth.security.CustomUserDetails;
import com.example.couplead.common.response.ApiResponse;
import com.example.couplead.weather.dto.response.HourlyWeatherResponse;
import com.example.couplead.weather.service.WeatherService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/weather")
public class WeatherController {

    private final WeatherService weatherService;

    @GetMapping("/partner/hourly")
    public ApiResponse<HourlyWeatherResponse> getPartnerHourlyWeather(
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        return ApiResponse.success(
                weatherService
                        .getPartnerHourlyForecast(
                                userDetails
                                        .getUser()
                                        .getId()));
    }
}