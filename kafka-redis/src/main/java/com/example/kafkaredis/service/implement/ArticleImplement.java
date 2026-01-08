package com.example.kafkaredis.service.implement;

import com.example.kafkaredis.dto.respose.UserViewEvent;
import com.example.kafkaredis.model.Article;
import com.example.kafkaredis.repository.ArticleRepository;
import com.example.kafkaredis.service.ArticleService;
import com.example.kafkaredis.service.KafkaProducerService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
    public class ArticleImplement implements ArticleService {
    private final ArticleRepository articleRepository;
    private final StringRedisTemplate redisTemplate;
    private final KafkaProducerService kafkaProducerService;

    @Override
    public Article getArticleDetail(Long articleId, Long userId) {
        // 1. Tìm bài viết trong DB
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bài viết"));

        String viewedKey = "article:viewed_users:" + articleId;
        String countKey = "article:views:" + articleId;

        // Lệnh 1: SADD article:viewed_users:101 50
        // Trả về 1 nếu user chưa có trong set, trả về 0 nếu đã có
        Long isNewViewer = redisTemplate.opsForSet().add(viewedKey, String.valueOf(userId));

        if (isNewViewer != null && isNewViewer > 0) {
            // Lệnh 2: INCR article:views:101
            Long currentViews = redisTemplate.opsForValue().increment(countKey);

            // Lệnh 3: Bắn tin nhắn lên Kafka (Sử dụng Event DTO đã tạo trước đó)
            UserViewEvent event = UserViewEvent.builder()
                    .userId(userId)
                    .articleId(articleId)
                    .viewedAt(System.currentTimeMillis())
                    .build();
            kafkaProducerService.sendViewEvent(event);

            // Cập nhật giá trị hiển thị tạm thời cho object trả về
            article.setTotalViews(currentViews);
        } else {

            String viewsStr = redisTemplate.opsForValue().get(countKey);
            article.setTotalViews(viewsStr != null ? Long.parseLong(viewsStr) : article.getTotalViews());
        }

        return article;
    }
}
