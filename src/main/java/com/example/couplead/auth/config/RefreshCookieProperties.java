package com.example.couplead.auth.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "auth.cookie")
public record RefreshCookieProperties(
        boolean secure,
        String sameSite,
        long maxAgeDays
) {
}