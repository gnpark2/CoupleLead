package com.example.couplead.event.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.example.couplead.event.dto.CoupleAnniversaryUpdatedEvent;
import com.example.couplead.widget.service.WidgetCacheService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class CoupleAnniversaryConsumer {
    private final WidgetCacheService widgetCacheService;

    @KafkaListener(
        topics = "couple-anniversary-updated",
        groupId = "widget-cache-group"
    )
    public void consume(
        CoupleAnniversaryUpdatedEvent event
    ) {
        log.info(
            "기념일 이벤트 수신: {}",
            event
        );

        widgetCacheService.updateCache(
            event.coupleId(),
            event.anniversary()
        );
    }
}
