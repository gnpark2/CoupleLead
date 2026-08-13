package com.example.couplead.auth.util;

import java.time.Duration;

import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import com.example.couplead.auth.config.RefreshCookieProperties;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RefreshTokenCookieUtil {

    public static final String COOKIE_NAME =
            "refreshToken";

    private static final String COOKIE_PATH =
            "/api/auth/web";

    private final RefreshCookieProperties properties;

    public ResponseCookie create(
            String refreshToken
    ) {

        return ResponseCookie
                .from(
                        COOKIE_NAME,
                        refreshToken
                )
                .httpOnly(true)
                .secure(properties.secure())
                .sameSite(properties.sameSite())
                .path(COOKIE_PATH)
                .maxAge(
                        Duration.ofDays(
                                properties.maxAgeDays()
                        )
                )
                .build();
    }

    public ResponseCookie delete() {

        return ResponseCookie
                .from(
                        COOKIE_NAME,
                        ""
                )
                .httpOnly(true)
                .secure(properties.secure())
                .sameSite(properties.sameSite())
                .path(COOKIE_PATH)
                .maxAge(Duration.ZERO)
                .build();
    }
}