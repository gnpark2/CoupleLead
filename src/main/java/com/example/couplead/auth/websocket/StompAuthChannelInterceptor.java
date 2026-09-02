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
public class StompAuthChannelInterceptor
        implements ChannelInterceptor {

    private final JwtProvider jwtProvider;

    @Override
    public Message<?> preSend(
            Message<?> message,
            MessageChannel channel) {

        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(
                message,
                StompHeaderAccessor.class);

        if (accessor == null) {
            return message;
        }

        StompCommand command = accessor.getCommand();

        /*
         * 1. CONNECT 시 JWT 인증
         */
        if (StompCommand.CONNECT.equals(
                command)) {

            String authHeader = accessor.getFirstNativeHeader(
                    "Authorization");

            log.info(
                    "[STOMP AUTH] CONNECT "
                            + "sessionId={} "
                            + "authorizationPresent={}",
                    accessor.getSessionId(),
                    authHeader != null);

            /*
             * 중요:
             * 인증 헤더가 없는데 연결을 허용하면
             * 이후 SEND에서 Principal=null이 된다.
             */
            if (authHeader == null ||
                    !authHeader.startsWith(
                            "Bearer ")) {

                log.warn(
                        "[STOMP AUTH] CONNECT REJECTED "
                                + "Authorization 없음 "
                                + "sessionId={}",
                        accessor.getSessionId());

                throw new IllegalArgumentException(
                        "STOMP Authorization header가 없습니다.");
            }

            String token = authHeader.substring(
                    7);

            if (!jwtProvider.validateToken(
                    token)) {

                log.warn(
                        "[STOMP AUTH] CONNECT REJECTED "
                                + "유효하지 않은 JWT "
                                + "sessionId={}",
                        accessor.getSessionId());

                throw new IllegalArgumentException(
                        "유효하지 않은 STOMP JWT입니다.");
            }

            Long userId = jwtProvider.extractUserId(
                    token);

            WebSocketPrincipal principal = new WebSocketPrincipal(
                    userId);

            /*
             * 가장 중요한 부분.
             *
             * Spring이 이 Principal을
             * 같은 STOMP session의
             * SEND/SUBSCRIBE 등에 연결한다.
             */
            accessor.setUser(
                    principal);

            log.info(
                    "[STOMP AUTH] CONNECT SUCCESS "
                            + "sessionId={} "
                            + "userId={}",
                    accessor.getSessionId(),
                    userId);
        }

        /*
         * 2. SEND 진단
         */
        if (StompCommand.SEND.equals(
                command)) {

            log.info(
                    "[STOMP AUTH] SEND "
                            + "sessionId={} "
                            + "user={}",
                    accessor.getSessionId(),
                    accessor.getUser());

            /*
             * 여기서 null이면
             * CONNECT 인증이 정상적으로
             * 유지되지 않은 것.
             */
            if (accessor.getUser() == null) {

                log.warn(
                        "[STOMP AUTH] SEND REJECTED "
                                + "Principal=null "
                                + "sessionId={}",
                        accessor.getSessionId());

                throw new IllegalStateException(
                        "인증되지 않은 STOMP 세션입니다.");
            }
        }

        return message;
    }
}