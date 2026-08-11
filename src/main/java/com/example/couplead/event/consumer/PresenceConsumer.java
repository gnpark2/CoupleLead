package com.example.couplead.event.consumer;

import com.example.couplead.event.producer.WidgetRefreshProducer;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.example.couplead.couple.domain.CoupleMember;
import com.example.couplead.couple.repository.CoupleMemberRepository;
import com.example.couplead.presence.dto.UserPresenceEvent;
import com.example.couplead.user.domain.User;
import com.example.couplead.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class PresenceConsumer {
    private final WidgetRefreshProducer widgetRefreshProducer;
    private final SimpMessagingTemplate messagingTemplate;
    private final UserRepository userRepository;
    private final CoupleMemberRepository coupleMemberRepository;

    @KafkaListener(
        topics = "user-presence",
        groupId = "presence-group"
    )
    @Transactional(readOnly = true)
    public void consume(UserPresenceEvent event) {
        log.info("온라인 상태 이벤트: {}", event);
        // websocket으로 presence 이벤트 전달
        messagingTemplate.convertAndSend("/topic/presence/" + event.userId(), event);

        // 사용자 조회
        User user = userRepository.findById(event.userId()).orElse(null);

        if (user == null) {
            return;
        }

        CoupleMember coupleMember = coupleMemberRepository.findByUser(user).orElse(null);

        if (coupleMember == null) {
            return;
        }

        Long coupleId = coupleMember.getCouple().getId();

        // 캐시 무효화
        widgetRefreshProducer.publish(coupleId, "PRESENCE_CHANGED");
    }
}
