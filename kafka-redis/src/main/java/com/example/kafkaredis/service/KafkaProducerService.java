package com.example.kafkaredis.service;

import com.example.kafkaredis.configuration.KafkaTopicConfig;
import com.example.kafkaredis.dto.respose.UserViewEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper; // Import của Jackson

@Service
@Slf4j
public class KafkaProducerService {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public KafkaProducerService(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendViewEvent(UserViewEvent event) {
        try {
            // Sử dụng articleId làm Key để các tin nhắn của cùng 1 bài viết luôn vào cùng 1 partition (đảm bảo thứ tự)
            kafkaTemplate.send(KafkaTopicConfig.VIEW_LOG_TOPIC, String.valueOf(event.getArticleId()), event);
            log.info("Sent view event to Kafka: User {} viewed Article {}", event.getUserId(), event.getArticleId());
        } catch (Exception e) {
            log.error("Error sending view event to Kafka", e);
        }
    }
}
