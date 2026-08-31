package com.example.couplead.chat.event;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.example.couplead.chat.realtime.ChatRealtimeEventType;
import com.example.couplead.chat.realtime.ChatRealtimeRedisPublisher;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ChatAnnouncementChangedListener {
        private final ChatRealtimeRedisPublisher chatRealtimeRedisPublisher;


        @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
        public void handle(
                        ChatAnnouncementChangedEvent event) {
                chatRealtimeRedisPublisher.publish(
                                ChatRealtimeEventType.ANNOUNCEMENT,
                                event.coupleId(),
                                event);
        }
}