package com.example.couplead.user.event;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.example.couplead.event.producer.WidgetRefreshProducer;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProfileUpdatedListener {

    private final WidgetRefreshProducer widgetRefreshProducer;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(
            ProfileUpdatedEvent event) {
        System.out.println(
                "[PROFILE UPDATE] AFTER_COMMIT coupleId="
                        + event.coupleId());

        widgetRefreshProducer.publish(
                event.coupleId(),
                "PROFILE_UPDATE");
    }
}