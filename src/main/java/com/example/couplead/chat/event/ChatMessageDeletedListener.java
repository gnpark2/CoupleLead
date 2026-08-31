package com.example.couplead.chat.event;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.example.couplead.chat.domain.MessageType;
import com.example.couplead.chat.dto.response.ChatMessageDeletedResponse;
import com.example.couplead.chat.realtime.ChatRealtimeEventType;
import com.example.couplead.chat.realtime.ChatRealtimeRedisPublisher;
import com.example.couplead.chat.repository.MessageSearchRepository;
import com.example.couplead.chat.service.ChatCacheService;
import com.example.couplead.chat.service.ChatImageStorageService;
import com.example.couplead.event.producer.WidgetRefreshProducer;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ChatMessageDeletedListener {

        private final ChatImageStorageService chatImageStorageService;

        private final MessageSearchRepository messageSearchRepository;

        private final ChatCacheService chatCacheService;

        private final WidgetRefreshProducer widgetRefreshProducer;
        private final ChatRealtimeRedisPublisher chatRealtimeRedisPublisher;

        @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
        public void handle(
                        ChatMessageDeletedEvent event) {
                /*
                 * 1. IMAGE 메시지라면
                 * 실제 이미지 파일 삭제
                 */
                if (event.type() == MessageType.IMAGE) {

                        chatImageStorageService.delete(
                                        event.imagePath());
                }

                /*
                 * 2. Elasticsearch 문서 제거
                 */
                messageSearchRepository.deleteById(
                                event.messageId()
                                                .toString());

                /*
                 * 3. 최근 채팅 Redis cache 무효화
                 */
                chatCacheService.invalidate(
                                event.coupleId());

                /*
                 * 4. A / B에게 삭제 이벤트 전송
                 */
                ChatMessageDeletedResponse response = new ChatMessageDeletedResponse(
                                event.coupleId(),
                                event.messageId(),
                                event.deletedAt());

                chatRealtimeRedisPublisher.publish(
                                ChatRealtimeEventType.DELETE,
                                event.coupleId(),
                                response);

                /*
                 * 5. Home widget도 최신화
                 */
                widgetRefreshProducer.publish(
                                event.coupleId(),
                                "CHAT_DELETE");
        }
}