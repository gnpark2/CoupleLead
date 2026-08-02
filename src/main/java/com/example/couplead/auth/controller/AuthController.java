package com.example.couplead.auth.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.ResponseEntity;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import com.example.couplead.auth.dto.request.LoginRequest;
import com.example.couplead.auth.dto.request.LogoutRequest;
import com.example.couplead.auth.dto.request.ReissueRequest;
import com.example.couplead.auth.dto.request.SignupRequest;
import com.example.couplead.auth.dto.response.LoginResponse;
import com.example.couplead.auth.dto.response.SignupResponse;
import com.example.couplead.auth.dto.response.TokenResponse;
import com.example.couplead.user.service.UserService;
import com.example.couplead.common.response.ApiResponse;
import com.example.couplead.auth.service.AuthService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {
    private final UserService userService;
    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<SignupResponse>> signup(
        @Valid @RequestBody SignupRequest request
    ) {
        SignupResponse response = userService.signup(request);
        return ResponseEntity.ok(
            ApiResponse.success(response)
        );
    }

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.success(authService.login(request));
    }

    @PostMapping("/reissue")
    public ApiResponse<TokenResponse> reissue(@Valid @RequestBody ReissueRequest request) {
        return ApiResponse.success(authService.reissue(request));
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout(@Valid @RequestBody LogoutRequest request) {
        authService.logout(request.refreshToken());
        return ApiResponse.success();
    }
}
