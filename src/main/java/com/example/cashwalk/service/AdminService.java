package com.example.cashwalk.service;

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
}
