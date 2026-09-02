package com.example.couplead.domain.dto.request;

import com.example.couplead.domain.entity.DevicePlatform;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record DeviceRegisterRequest(

        @NotBlank String fid,

        @NotBlank String fcmToken,

        @NotNull DevicePlatform platform

) {
}