package com.example.couplead.chat.event;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.example.couplead.event.producer.ChatReadEventProducer;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ChatReadCommittedListener {

    private final ChatReadEventProducer
            chatReadEventProducer;

    @TransactionalEventListener(
            phase = TransactionPhase.AFTER_COMMIT
    )
    public void handle(
            ChatReadCommittedEvent event
    ) {
        chatReadEventProducer.publish(
                event.event()
        );
    }
}