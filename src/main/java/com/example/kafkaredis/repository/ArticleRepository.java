package com.example.kafkaredis.repository;

import com.example.kafkaredis.model.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface ArticleRepository extends JpaRepository<Article, Long> {
    @Modifying
    @Query("UPDATE Article a SET a.totalViews = a.totalViews + 1 WHERE a.id = :id")
    void incrementTotalViews(Long id);
}