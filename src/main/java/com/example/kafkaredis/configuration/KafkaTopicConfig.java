package com.example.kafkaredis.configuration;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {
    public static final String VIEW_LOG_TOPIC = "article_view_events";
    @Bean
    public NewTopic articleViewTopic() {
        return TopicBuilder.name(VIEW_LOG_TOPIC)
                .partitions(3) // Chia 3 partition để có thể chạy nhiều Consumer song song
                .replicas(1)   // Số bản sao (tùy thuộc vào số lượng Broker bạn có)
                .build();
    }
}
