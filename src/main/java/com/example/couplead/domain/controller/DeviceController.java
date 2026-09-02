package com.example.couplead.domain.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.couplead.auth.security.CustomUserDetails;
import com.example.couplead.common.response.ApiResponse;
import com.example.couplead.domain.dto.request.DeviceRegisterRequest;
import com.example.couplead.domain.service.DeviceService;
import com.example.couplead.domain.service.FirebasePushService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/devices")
@RequiredArgsConstructor
public class DeviceController {

    private final DeviceService deviceService;
    private final FirebasePushService firebasePushService;

    @PostMapping
    public ApiResponse<Void> register(
            @AuthenticationPrincipal CustomUserDetails userDetails,

            @Valid @RequestBody DeviceRegisterRequest request) {

        deviceService.register(
                userDetails.getUser().getId(),
                request);

        return ApiResponse.success(
                null);
    }

    @PostMapping("/push/test")
    public ApiResponse<Void> testPush(
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        firebasePushService.sendToUser(
                userDetails.getUser().getId(),
                "Couplead",
                "FCM 테스트 알림입니다.",
                1L,
                1L);

        return ApiResponse.success(
                null);
    }
}