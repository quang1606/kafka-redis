package com.example.kafkaredis.controller;

import com.example.kafkaredis.model.Article;
import com.example.kafkaredis.repository.UserRepository;
import com.example.kafkaredis.service.ArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Random;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/article")
public class ArticleController {

    private final ArticleService articleService;
    private final UserRepository userRepository;

    @GetMapping("/{id}")
    public ResponseEntity<Article> viewArticle(@PathVariable Long id) {

        List<Long> allUserIds = userRepository.findAll().stream()
                .map(user -> user.getId())
                .toList();

        if (allUserIds.isEmpty()) {
            throw new RuntimeException("Chưa có dữ liệu User mẫu. Hãy chạy Test JavaFaker trước!");
        }

        Long randomUserId = allUserIds.get(new Random().nextInt(allUserIds.size()));

        // 2. Gọi Service xử lý
        Article article = articleService.getArticleDetail(id, randomUserId);

        return ResponseEntity.ok(article);
    }
}
