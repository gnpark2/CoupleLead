package com.example.couplead.chat.kafka;

import com.example.couplead.chat.event.ChatPushEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatPushProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void send(
            ChatPushEvent event) {

        String key = String.valueOf(
                event.receiverId());

        kafkaTemplate.send(
                ChatKafkaTopics.CHAT_PUSH,
                key,
                event).whenComplete(
                        (result, throwable) -> {

                            if (throwable != null) {

                                log.error(
                                        "[CHAT PUSH PRODUCER] "
                                                + "발행 실패 "
                                                + "coupleId={} "
                                                + "senderId={} "
                                                + "receiverId={}",
                                        event.coupleId(),
                                        event.senderId(),
                                        event.receiverId(),
                                        throwable);

                                return;
                            }

                            log.info(
                                    "[CHAT PUSH PRODUCER] "
                                            + "발행 성공 "
                                            + "coupleId={} "
                                            + "senderId={} "
                                            + "receiverId={}",
                                    event.coupleId(),
                                    event.senderId(),
                                    event.receiverId());
                        });
    }
}