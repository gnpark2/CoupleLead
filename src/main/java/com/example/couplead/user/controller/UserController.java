package com.example.couplead.user.controller;

import java.util.Map;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;

import com.example.couplead.auth.security.CustomUserDetails;
import com.example.couplead.common.response.ApiResponse;
import com.example.couplead.user.dto.request.UpdateLocationRequest;
import com.example.couplead.user.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    @GetMapping("/me")
    public ApiResponse<String> me(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ApiResponse.success(
                userDetails.getUser().getEmail());
    }

    @PatchMapping("/me/location")
    public ApiResponse<Void> updateLocation(@AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody UpdateLocationRequest request) {
        userService.updateLocation(
                userDetails.getUser().getId(),
                request);

        return ApiResponse.success();
    }

    @PostMapping("/me/location-debug")
    public ApiResponse<Map<String, Object>> debugLocation(
            @RequestBody Map<String, Object> body) {
        System.out.println("BODY = " + body);

        return ApiResponse.success(body);
    }

}
