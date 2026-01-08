    package com.example.kafkaredis.service.eventservice;

    import com.example.kafkaredis.configuration.KafkaTopicConfig;
    import com.example.kafkaredis.dto.respose.UserViewEvent;
    import com.example.kafkaredis.model.ViewLog;
    import com.example.kafkaredis.repository.ArticleRepository;
    import com.example.kafkaredis.repository.UserRepository;
    import com.example.kafkaredis.repository.ViewLogRepository;
    import jakarta.transaction.Transactional;
    import lombok.AllArgsConstructor;
    import lombok.extern.slf4j.Slf4j;
    import org.springframework.kafka.annotation.DltHandler;
    import org.springframework.kafka.annotation.KafkaListener;
    import org.springframework.kafka.annotation.RetryableTopic;
    import org.springframework.kafka.retrytopic.DltStrategy;
    import org.springframework.kafka.support.KafkaHeaders;
    import org.springframework.messaging.handler.annotation.Header;
    import org.springframework.retry.annotation.Backoff;
    import org.springframework.stereotype.Component;

    import java.sql.SQLException;
    import java.time.Instant;
    import java.time.LocalDateTime;
    import java.util.TimeZone;

    @Component
    @Slf4j
    @AllArgsConstructor
    public class ViewLogConsumer {
        private final ViewLogRepository viewLogRepository;
        private final ArticleRepository articleRepository;
        private final UserRepository userRepository;
        @RetryableTopic(
                attempts = "3", // Thử lại tối đa 3 lần
                backoff = @Backoff(delay = 2000, multiplier = 5.0), // Đợi 2s, 10s, 50s
                dltStrategy = DltStrategy.FAIL_ON_ERROR, // Lỗi thì bắn vào DLT
                include = {RuntimeException.class, SQLException.class} // Các loại lỗi cần retry
        )
        @KafkaListener(topics = KafkaTopicConfig.VIEW_LOG_TOPIC, groupId = "view_log_group")
        @Transactional
        public void consume(UserViewEvent event) {
            log.info("Received event: User {} viewed Article {}", event.getUserId(), event.getArticleId());

            try {
                // 1. Lưu vào bảng view_logs

                ViewLog logEntry = ViewLog.builder()
                        .user(userRepository.getReferenceById(event.getUserId())) // Trả về User trực tiếp (proxy)
                        .article(articleRepository.getReferenceById(event.getArticleId()))
                        .viewedAt(LocalDateTime.ofInstant(Instant.ofEpochMilli(event.getViewedAt()), TimeZone.getDefault().toZoneId()))
                        .build();
                viewLogRepository.save(logEntry);
                // 2. Cập nhật tổng view trong bảng articles
                articleRepository.incrementTotalViews(event.getArticleId());

                log.info("Successfully updated DB for article: {}", event.getArticleId());
            } catch (Exception e) {
                log.error("Failed to process view event", e);
                throw new RuntimeException("Database is busy!");
            }
        }
        // Đây là "thùng rác" để hứng tin nhắn lỗi cuối cùng
        @DltHandler
        public void handleDltMessage(String message, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
            log.error("TIN NHẮN LỖI NẶNG - Đã đẩy vào DLT [{}]: {}", topic, message);
            // notificationService.sendTelegram("Hệ thống tăng view đang lỗi tin nhắn: " + message);
        }

    }
