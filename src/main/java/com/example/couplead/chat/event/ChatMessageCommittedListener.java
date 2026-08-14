package com.example.couplead.chat.event;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import com.example.couplead.event.producer.WidgetRefreshProducer;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ChatMessageCommittedListener {

    private final SimpMessagingTemplate messagingTemplate;
    private final WidgetRefreshProducer widgetRefreshProducer;

    @TransactionalEventListener(
            phase = TransactionPhase.AFTER_COMMIT
    )
    public void handle(
            ChatMessageCommittedEvent event
    ) {

        /*
         * MySQL COMMIT이 끝난 다음에만
         * Flutter에게 메시지를 알린다.
         */
        messagingTemplate.convertAndSend(
                "/topic/chat/" + event.coupleId(),
                event.message()
        );

        widgetRefreshProducer.publish(
                event.coupleId(),
                "CHAT_MESSAGE"
        );
    }
}