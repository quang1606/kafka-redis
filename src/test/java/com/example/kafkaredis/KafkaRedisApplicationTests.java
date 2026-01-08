package com.example.kafkaredis;

import com.example.kafkaredis.model.Article;
import com.example.kafkaredis.model.User;
import com.example.kafkaredis.repository.ArticleRepository;
import com.example.kafkaredis.repository.UserRepository;
import com.github.javafaker.Faker;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import java.util.Locale;

@SpringBootTest
class KafkaRedisApplicationTests {

    @Test
    void contextLoads() {
    }
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ArticleRepository articleRepository;
    private final Faker faker = new Faker(new Locale("vi"));
    @Test
    @Order(1)
    @Rollback(false) // Giữ lại dữ liệu sau khi chạy test
    void generate_fake_users() {
        System.out.println("--- Đang tạo dữ liệu User ---");
        for (int i = 0; i < 50; i++) {
            User user = User.builder()
                    .username(faker.name().username())
                    .email(faker.internet().emailAddress())
                    .build();
            userRepository.save(user);
        }
        System.out.println("Đã tạo xong 50 users.");
    }

    @Test
    @Order(2)
    @Rollback(false)
    void generate_fake_articles() {
        System.out.println("--- Đang tạo dữ liệu Article ---");
        for (int i = 0; i < 30; i++) {
            Article article = Article.builder()
                    .title(faker.book().title())
                    // Tạo nội dung dài khoảng 5 đoạn văn
                    .content(faker.lorem().paragraph(10))
                    .totalViews(0L) // Mặc định là 0 như yêu cầu
                    .build();
            articleRepository.save(article);
        }
        System.out.println("Đã tạo xong 30 bài viết.");
    }

}
