package com.example.couplead.presence.listener;

import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import com.example.couplead.auth.websocket.WebSocketPrincipal;
import com.example.couplead.presence.service.PresenceService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketPresenceListener {
    private final PresenceService presenceService;

    @EventListener
    public void connect(SessionConnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());

        if(accessor.getUser() instanceof WebSocketPrincipal principal) {
            log.info("WebSocket connect: {}", principal.getUserId());

            presenceService.connect(principal.getUserId());
        }
    }

    @EventListener
    public void disconnect(SessionDisconnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());

        if (accessor.getUser() instanceof WebSocketPrincipal principal) {
            log.info("WebSocket disconnect: {}",
                principal.getUserId());

            presenceService.disconnect(principal.getUserId());
        }
    }
}
