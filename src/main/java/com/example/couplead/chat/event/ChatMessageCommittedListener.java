package com.example.couplead.chat.event;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.example.couplead.chat.realtime.ChatRealtimeEventType;
import com.example.couplead.chat.realtime.ChatRealtimeRedisPublisher;
import com.example.couplead.chat.repository.MessageRepository;
import com.example.couplead.chat.service.ChatUnreadCacheService;
import com.example.couplead.couple.domain.Couple;
import com.example.couplead.couple.repository.CoupleMemberRepository;
import com.example.couplead.couple.repository.CoupleRepository;
import com.example.couplead.event.producer.WidgetRefreshProducer;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ChatMessageCommittedListener {

        private final ChatRealtimeRedisPublisher chatRealtimeRedisPublisher;
        private final WidgetRefreshProducer widgetRefreshProducer;
        private final CoupleMemberRepository coupleMemberRepository;
        private final CoupleRepository coupleRepository;
        private final MessageRepository messageRepository;
        private final ChatUnreadCacheService chatUnreadCacheService;

        private Long findPartnerUserId(
                        Long coupleId,
                        Long senderId) {
                Couple couple = coupleRepository
                                .findById(coupleId)
                                .orElseThrow();

                return coupleMemberRepository
                                .findByCoupleWithUser(couple)
                                .stream()
                                .map(member -> member.getUser()
                                                .getId())
                                .filter(userId -> !userId.equals(
                                                senderId))
                                .findFirst()
                                .orElseThrow();
        }

        @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
        public void handle(
                        ChatMessageCommittedEvent event) {

                Long senderId = event.message()
                                .senderId();

                Long receiverId = findPartnerUserId(
                                event.coupleId(),
                                senderId);

                /*
                 * MySQL이 기준이다.
                 *
                 * Redis에서 INCR 하지 않고
                 * 실제 DB unread 수를 계산해서 SET.
                 */
                long unreadCount = messageRepository
                                .countByCoupleIdAndSenderIdNotAndReadAtIsNull(
                                                event.coupleId(),
                                                receiverId);

                chatUnreadCacheService.set(
                                receiverId,
                                event.coupleId(),
                                unreadCount);

                /*
                 * 다중 서버 실시간 메시지 fan-out
                 */
                chatRealtimeRedisPublisher.publish(
                                ChatRealtimeEventType.MESSAGE,
                                event.coupleId(),
                                event.message());

                widgetRefreshProducer.publish(
                                event.coupleId(),
                                "CHAT_MESSAGE");
        }
}