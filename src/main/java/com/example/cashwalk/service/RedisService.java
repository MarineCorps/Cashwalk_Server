package com.example.cashwalk.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisService {

    private final RedisTemplate<String, String> redisTemplate;

    // 사용자가 해당 게시글을 이미 조회했는지 확인
    public boolean hasViewPost(Long userId, Long postId) {
        String key = generateKey(userId, postId);
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    // 사용자가 해당 게시글을 조회했음을 Redis에 기록 (1시간 유지)
    public void markPostAsViewed(Long userId, Long postId) {
        String key = generateKey(userId, postId);
        redisTemplate.opsForValue().set(key, "viewed", 1, TimeUnit.HOURS); // ⏱️ 1시간 유지
    }

    // Redis key 생성 규칙: viewed:userId:postId
    private String generateKey(Long userId, Long postId) {
        return "viewed:" + userId + ":" + postId;
    }
}
