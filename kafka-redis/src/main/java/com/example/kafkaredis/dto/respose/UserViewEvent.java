package com.example.kafkaredis.dto.respose;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
@AllArgsConstructor
@Builder
@Getter
@NoArgsConstructor
public class UserViewEvent {
    private Long userId;
    private Long articleId;
    private Long viewedAt; // Timestamp
}
