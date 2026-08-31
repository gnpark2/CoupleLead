package com.example.couplead.chat.event;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.example.couplead.chat.dto.response.ChatMessageEditedResponse;
import com.example.couplead.chat.realtime.ChatRealtimeEventType;
import com.example.couplead.chat.realtime.ChatRealtimeRedisPublisher;
import com.example.couplead.chat.repository.MessageSearchRepository;
import com.example.couplead.chat.service.ChatCacheService;
import com.example.couplead.event.producer.WidgetRefreshProducer;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ChatMessageEditedListener {

        private final ChatCacheService chatCacheService;
        private final WidgetRefreshProducer widgetRefreshProducer;
        private final MessageSearchRepository messageSearchRepository;
        private final ChatRealtimeRedisPublisher chatRealtimeRedisPublisher;

        @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
        public void handle(
                        ChatMessageEditedEvent event) {
                /*
                 * Redis 최신 채팅 캐시는
                 * 수정 전 JSON을 갖고 있을 수 있으므로 제거
                 */
                chatCacheService.invalidate(
                                event.coupleId());

                /*
                 * A/B 채팅 화면 즉시 갱신
                 */
                ChatMessageEditedResponse response = new ChatMessageEditedResponse(
                                event.coupleId(),
                                event.messageId(),
                                event.content(),
                                event.editedAt());

                chatRealtimeRedisPublisher.publish(
                                ChatRealtimeEventType.EDIT,
                                event.coupleId(),
                                response);

                /*
                 * 홈 최신 메시지 등도 갱신
                 */
                widgetRefreshProducer.publish(
                                event.coupleId(),
                                "CHAT_EDIT");

                messageSearchRepository
                                .findById(
                                                event.messageId()
                                                                .toString())
                                .ifPresent(
                                                document -> {
                                                        document.updateContent(
                                                                        event.content());

                                                        messageSearchRepository.save(
                                                                        document);
                                                });
        }
}