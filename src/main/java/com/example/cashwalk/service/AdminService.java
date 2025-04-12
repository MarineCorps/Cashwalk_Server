package com.example.cashwalk.service;
// 필요한 import 추가
import com.example.cashwalk.dto.ModifyPostStatsRequest;
import com.example.cashwalk.entity.Post;
import com.example.cashwalk.entity.PostLike;
import com.example.cashwalk.repository.PostRepository;
import com.example.cashwalk.repository.PostLikeRepository;

import org.springframework.transaction.annotation.Transactional;
import com.example.cashwalk.dto.ModifyPointsRequest;
import com.example.cashwalk.dto.UserDto;
import com.example.cashwalk.entity.User;
import com.example.cashwalk.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service // 🧠 이 클래스는 서비스 역할 (비즈니스 로직 처리)
@RequiredArgsConstructor // 🧠 생성자 자동 주입
public class AdminService {

    private final UserRepository userRepository;
    private final PointsService pointsService;
    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;

    /**
     * ✅ 전체 사용자 목록을 조회하는 메서드
     */
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserDto::from) // User → UserDto로 변환
                .collect(Collectors.toList());
    }

    /**
     * ✅ 특정 사용자에게 포인트를 지급 또는 차감하는 메서드
     */
    public void modifyUserPoints(ModifyPointsRequest request) {
        // 사용자 찾기
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        int amount = request.getAmount(); // 지급(+) 또는 차감(-)
        String description = request.getDescription(); // 설명 메시지

        // 포인트 지급 서비스 호출 (나중에 구현할 메서드)
        pointsService.addReward(user, amount, description);
    }
    public void resetUserPoints(Long userId) {
        //1. 사용자 찾기
        User user=userRepository.findById(userId)
                .orElseThrow(()->new IllegalArgumentException("사용자를 찾을 수없습니다."));
        //2. 로직 수행
        pointsService.resetPoints(user); //핵심 로직 추출
    }
    public void deleteUser(Long userId){
        User user=userRepository.findById(userId)
                .orElseThrow(()->new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        userRepository.delete(user); //추후 cascade여부에 따라 예외발생가능
    }


    /**
     * ✅ 관리자 전용: 게시글 좋아요/조회수 조작
     */
    @Transactional
    public void modifyPostStats(ModifyPostStatsRequest request) {
        Post post = postRepository.findById(request.getPostId())
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글을 찾을 수 없습니다."));

        // 좋아요 수 조작
        if (request.getLikeCount() != null) {
            // 기존 좋아요 삭제 (LIKE만)
            postLikeRepository.deleteAllByPostIdAndStatus(post.getId(), PostLike.Status.LIKE);

            // 가짜 유저 ID로 가상의 좋아요 입력
            for (int i = 1; i <= request.getLikeCount(); i++) {
                PostLike like = PostLike.builder()
                        .post(post)
                        .status(PostLike.Status.LIKE)
                        .user(null) // 테스트용 가상 유저 → Controller에서 채우는 방식도 가능
                        .build();
                postLikeRepository.save(like);
            }
        }

        // 조회수 조작
        if (request.getViewCount() != null) {
            post.setViews(request.getViewCount());
        }

        postRepository.save(post);
    }
}
