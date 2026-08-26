package com.example.couplead.couple.event;

import lombok.RequiredArgsConstructor;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class CoupleConnectedEventListener {

    private final SimpMessagingTemplate messagingTemplate;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(
            CoupleConnectedEvent event) {
        Map<String, Object> payload = Map.of(
                "type", "COUPLE_CONNECTED",
                "coupleId", event.coupleId());

        /*
         * 초대코드 주인
         */
        messagingTemplate.convertAndSend(
                "/topic/couple/user/" + event.userAId(),
                payload);

        /*
         * 초대코드를 입력한 사용자
         */
        messagingTemplate.convertAndSend(
                "/topic/couple/user/" + event.userBId(),
                payload);
    }
}