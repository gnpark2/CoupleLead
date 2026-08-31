package com.example.couplead.chat.event;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.example.couplead.chat.realtime.ChatRealtimeEventType;
import com.example.couplead.chat.realtime.ChatRealtimeRedisPublisher;
import com.example.couplead.event.producer.WidgetRefreshProducer;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ChatMessageCommittedListener {

        private final ChatRealtimeRedisPublisher chatRealtimeRedisPublisher;
        private final WidgetRefreshProducer widgetRefreshProducer;

        @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
        public void handle(
                        ChatMessageCommittedEvent event) {

                chatRealtimeRedisPublisher.publish(
                                ChatRealtimeEventType.MESSAGE,
                                event.coupleId(),
                                event.message());

                widgetRefreshProducer.publish(
                                event.coupleId(),
                                "CHAT_MESSAGE");
        }
}