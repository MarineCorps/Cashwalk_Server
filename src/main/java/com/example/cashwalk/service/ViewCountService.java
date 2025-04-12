package com.example.cashwalk.service;

import com.example.cashwalk.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ViewCountService {

    private final PostRepository postRepository;
    private final RedisService redisService;

    @Transactional
    public void increaseIfNotDuplicate(Long userId, Long postId) {
        if (!redisService.hasViewPost(userId, postId)) {
            System.out.println("Redis에 기록 없음 → 조회수 증가");
            postRepository.incrementViewCount(postId);
            redisService.markPostAsViewed(userId, postId);
        } else {
            System.out.println("이미 조회한 게시글 → 조회수 증가 안 함");
        }

        System.out.println("Redis Key: viewed:" + userId + ":" + postId);
    }
}
