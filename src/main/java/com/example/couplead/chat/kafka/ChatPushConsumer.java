package com.example.couplead.chat.kafka;

import com.example.couplead.chat.event.ChatPushEvent;
import com.example.couplead.domain.service.FirebasePushService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatPushConsumer {

    private final FirebasePushService firebasePushService;

    @KafkaListener(topics = ChatKafkaTopics.CHAT_PUSH, groupId = "chat-push-group")
    public void consume(
            ChatPushEvent event) {

        try {

            String body = buildPushBody(
                    event);

            firebasePushService.sendToUser(
                    event.receiverId(),
                    event.senderNickname(),
                    body,
                    event.coupleId(),
                    event.senderId());

            log.info(
                    "[CHAT PUSH CONSUMER] "
                            + "처리 완료 "
                            + "coupleId={} "
                            + "receiverId={}",
                    event.coupleId(),
                    event.receiverId());

        } catch (Exception e) {

            log.error(
                    "[CHAT PUSH CONSUMER] "
                            + "처리 실패 "
                            + "coupleId={} "
                            + "receiverId={}",
                    event.coupleId(),
                    event.receiverId(),
                    e);
        }
    }

    private String buildPushBody(
            ChatPushEvent event) {

        if (event.type() != null &&
                "IMAGE".equalsIgnoreCase(
                        event.type().name())) {

            return "사진을 보냈습니다.";
        }

        if (event.content() == null ||
                event.content().isBlank()) {

            return "새 메시지가 도착했습니다.";
        }

        return event.content();
    }
}