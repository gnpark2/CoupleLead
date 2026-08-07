package com.example.couplead.auth.websocket;

import java.security.Principal;

public class WebSocketPrincipal implements Principal {
    private final Long userId;

    public WebSocketPrincipal(Long userId) {
        this.userId = userId;
    }

    @Override
    public String getName() {
        return userId.toString();
    }

    public Long getUserId() {
        return userId;
    }
}
