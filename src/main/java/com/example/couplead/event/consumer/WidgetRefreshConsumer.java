package com.example.couplead.event.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.example.couplead.event.dto.WidgetRefreshEvent;
import com.example.couplead.widget.service.WidgetCacheService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class WidgetRefreshConsumer {
    private final WidgetCacheService widgetCacheService;

    @KafkaListener(
        topics = "widget-refresh",
        groupId = "widget-refresh-group"
    )
    public void consume(WidgetRefreshEvent event) {
        log.info(
            "Widget 캐시 무효화: coupleId={}, reason={}", event.coupleId(), event.reason()
        );

        widgetCacheService.invalidateByCouple(event.coupleId());
    }
}
