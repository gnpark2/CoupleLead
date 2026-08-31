package com.example.couplead.chat.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

import com.example.couplead.chat.realtime.ChatRealtimeChannel;
import com.example.couplead.chat.realtime.ChatRealtimeRedisSubscriber;

@Configuration
public class ChatRealtimeRedisConfig {

    @Bean
    public RedisMessageListenerContainer chatRealtimeRedisMessageListenerContainer(
            RedisConnectionFactory connectionFactory,
            ChatRealtimeRedisSubscriber subscriber) {

        RedisMessageListenerContainer container = new RedisMessageListenerContainer();

        container.setConnectionFactory(
                connectionFactory);

        container.addMessageListener(
                subscriber,
                new ChannelTopic(
                        ChatRealtimeChannel.CHANNEL));

        return container;
    }
}