package com.example.couplead.couple.event;

import java.util.Map;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CoupleDisconnectedEventListener {

    private final SimpMessagingTemplate messagingTemplate;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(
            CoupleDisconnectedEvent event) {
        Map<String, Object> payload = Map.of(
                "type",
                "COUPLE_DISCONNECTED",

                "coupleId",
                event.coupleId());

        messagingTemplate.convertAndSend(
                "/topic/couple/user/"
                        + event.userAId(),
                payload);

        messagingTemplate.convertAndSend(
                "/topic/couple/user/"
                        + event.userBId(),
                payload);
    }
}