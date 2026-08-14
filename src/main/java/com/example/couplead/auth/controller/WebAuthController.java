package com.example.couplead.auth.controller;

import java.time.Duration;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.example.couplead.auth.dto.request.LoginRequest;
import com.example.couplead.auth.dto.request.ReissueRequest;
import com.example.couplead.auth.dto.response.LoginResponse;
import com.example.couplead.auth.dto.response.TokenResponse;
import com.example.couplead.auth.dto.response.WebTokenResponse;
import com.example.couplead.auth.service.AuthService;
import com.example.couplead.auth.util.RefreshTokenCookieUtil;
import com.example.couplead.common.response.ApiResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth/web")
public class WebAuthController {

        private final AuthService authService;

        private final RefreshTokenCookieUtil refreshTokenCookieUtil;

        @PostMapping("/login")
        public ResponseEntity<ApiResponse<WebTokenResponse>> login(@Valid @RequestBody LoginRequest request) {

                LoginResponse tokens = authService.login(request);

                ResponseCookie cookie = refreshTokenCookieUtil.create(tokens.refreshToken());

                return ResponseEntity
                        .ok()
                        .header(
                                HttpHeaders.SET_COOKIE,
                                cookie.toString())
                        .body(
                                ApiResponse.success(
                                        new WebTokenResponse(
                                                tokens.accessToken())));
        }

        @PostMapping("/reissue")
        public ResponseEntity<ApiResponse<WebTokenResponse>> reissue(@CookieValue(name = RefreshTokenCookieUtil.COOKIE_NAME, required = false) String refreshToken) {

                if (refreshToken == null || refreshToken.isBlank()) {
                        throw new ResponseStatusException(
                                HttpStatus.UNAUTHORIZED,
                                "Refresh Token이 없습니다."
                        );
                }

                TokenResponse tokens = authService.reissue(
                        new ReissueRequest(
                                refreshToken));

                /*
                * Refresh Token Rotation
                */
                ResponseCookie cookie = refreshTokenCookieUtil.create(
                        tokens.refreshToken());

                return ResponseEntity
                        .ok()
                        .header(
                                HttpHeaders.SET_COOKIE,
                                cookie.toString())
                        .body(
                        ApiResponse.success(
                                new WebTokenResponse(tokens.accessToken())));
        }

        @PostMapping("/logout")
        public ResponseEntity<ApiResponse<Void>> logout(@CookieValue(value = "refreshToken", required = false) String refreshToken) {

                if (refreshToken != null && !refreshToken.isBlank()) {

                        authService.logout(refreshToken);
                }

                ResponseCookie expiredCookie = ResponseCookie
                        .from(
                                "refreshToken",
                                "")
                        .httpOnly(true)
                        .secure(false)
                        .sameSite("Lax")
                        .path("/api/auth/web")
                        .maxAge(Duration.ZERO)
                        .build();

                return ResponseEntity
                        .ok()
                        .header(
                                HttpHeaders.SET_COOKIE,
                                expiredCookie.toString())
                        .body(
                                ApiResponse.success());
        }
}