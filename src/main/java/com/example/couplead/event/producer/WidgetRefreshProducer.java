package com.example.couplead.event.producer;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.example.couplead.event.dto.WidgetRefreshEvent;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class WidgetRefreshProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publish(
        Long coupleId,
        String reason
    ) {
        WidgetRefreshEvent event =
            new WidgetRefreshEvent(
                coupleId,
                reason
            );

            kafkaTemplate.send(
                "widget-refresh",
                String.valueOf(coupleId),
                event
            );
    }
}
