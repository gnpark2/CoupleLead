package com.example.couplead.event.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaTopicConfig {
    
    @Bean
    NewTopic anniversaryTopic() {
        return new NewTopic("couple-anniversary-updated", 1, (short) 1);
    }

    @Bean
    NewTopic chatTopic() {
        return new NewTopic(
            "chat-message",
            1,
            (short) 1
        );
    }

    @Bean
    NewTopic chatReadTopic() {
        return new NewTopic(
            "chat-read",
            1,
            (short) 1
        );
    }
}
