package com.example.couplead.event.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
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

    private final SimpMessagingTemplate messagingTemplate;

    @KafkaListener(topics = "widget-refresh", groupId = "widget-refresh-group")
    public void consume(
            WidgetRefreshEvent event) {

        log.info(
                "Widget 캐시 무효화: "
                        + "coupleId={}, reason={}",
                event.coupleId(),
                event.reason());

        /*
         * 1. Redis Widget Cache 삭제
         */
        widgetCacheService
                .invalidateByCouple(
                        event.coupleId());

        /*
         * 2. 해당 커플의 Flutter Client에게
         * Widget 변경 알림
         */
        messagingTemplate.convertAndSend(
                "/topic/widget/"
                        + event.coupleId(),
                event);

        log.info(
                "Widget WebSocket 알림 전송: "
                        + "coupleId={}, reason={}",
                event.coupleId(),
                event.reason());
    }
}
