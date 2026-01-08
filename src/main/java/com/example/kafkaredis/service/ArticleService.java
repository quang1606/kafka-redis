package com.example.kafkaredis.service;

import com.example.kafkaredis.model.Article;

public interface ArticleService  {
    public Article getArticleDetail(Long articleId, Long userId) ;
}
