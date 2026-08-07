package com.example.couplead.auth.websocket;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

import com.example.couplead.auth.security.JwtProvider;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class StompAuthChannelInterceptor implements ChannelInterceptor {
    private final JwtProvider jwtProvider;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel){

    StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

    if (accessor == null) {
        return message;
    }

    if (StompCommand.CONNECT.equals(accessor.getCommand())) {
        String authHeader = accessor.getFirstNativeHeader("Authorization");

        log.info("STOMP CONNECT Authorization: {}", authHeader);

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            if (jwtProvider.validateToken(token)) {
                Long userId = jwtProvider.extractUserId(token);

                log.info("WebSocket authenticated user: {}", userId);

                WebSocketPrincipal principal = new WebSocketPrincipal(userId);

                accessor.setUser(principal);

                accessor.getSessionAttributes().put("userId", userId);
            }
        }
    }
    if (StompCommand.SEND.equals(accessor.getCommand())) {

        log.info("SEND user: {}", accessor.getUser());

        log.info("Session attributes: {}",
            accessor.getSessionAttributes());

        if (accessor.getUser() == null) {
            Long userId = (Long) accessor.getSessionAttributes().get("userId");

            if (userId != null) {
                accessor.setUser(new WebSocketPrincipal(userId));
            }
        }
    }
    return message;
    }
}
