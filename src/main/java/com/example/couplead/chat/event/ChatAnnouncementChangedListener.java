package com.example.couplead.chat.event;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ChatAnnouncementChangedListener {

    private final SimpMessagingTemplate messagingTemplate;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(
            ChatAnnouncementChangedEvent event) {
        messagingTemplate.convertAndSend(
                "/topic/chat/announcement/"
                        + event.coupleId(),
                event);
    }
}