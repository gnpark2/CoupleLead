package com.example.couplead.user.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.couplead.auth.security.CustomUserDetails;
import com.example.couplead.common.response.ApiResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {
    @GetMapping("/me")
    public ApiResponse<String> me(
        @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return ApiResponse.success(
            userDetails.getUser().getEmail()
        );
    }
}
