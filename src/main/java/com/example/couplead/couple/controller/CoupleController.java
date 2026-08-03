package com.example.couplead.couple.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.couplead.auth.security.CustomUserDetails;
import com.example.couplead.common.response.ApiResponse;
import com.example.couplead.couple.dto.request.ConnectRequest;
import com.example.couplead.couple.dto.response.InviteCodeResponse;
import com.example.couplead.couple.service.CoupleService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/couples")
public class CoupleController {
    private final CoupleService coupleService;

    @PostMapping("/invite")
    public ApiResponse<InviteCodeResponse> createInviteCode(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return ApiResponse.success(coupleService.createInviteCode(userDetails.getUser().getId()));
    }

    @PostMapping("/connect")
    public ApiResponse<Void> connect(@AuthenticationPrincipal CustomUserDetails userDetails, @Valid @RequestBody ConnectRequest request) {
        coupleService.connect(userDetails.getUser().getId(), request);
        return ApiResponse.success();
    }
}
