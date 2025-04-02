//포인트 적립 및 차감, 포인트 거래 내역 관리
package com.example.cashwalk.service;

import com.example.cashwalk.dto.PointsDto;
import com.example.cashwalk.dto.PointsHistoryDto;
import com.example.cashwalk.entity.Points;
import com.example.cashwalk.entity.User;
import com.example.cashwalk.repository.PointsRepository;
import com.example.cashwalk.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
/**
 * 포인트 비즈니스 로직 처리 클래스
 * - 현재 로그인한 사용자의 총 포인트 계산
 */
@Service // 🧠 이 클래스는 서비스 계층임을 Spring이 인식
@RequiredArgsConstructor // final 필드를 자동으로 생성자에 넣어줌
public class PointsService {

    private final PointsRepository pointsRepository;
    private final UserRepository userRepository;

    /**
     * 현재 로그인한 사용자의 포인트 총합 조회
     */
    public PointsDto getPointBalance(Long userId) {
        // 1. 사용자 정보 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

        // 2. 포인트 총합 계산
        int total = pointsRepository.getTotalPointsByUser(user);

        // 3. 응답용 DTO 반환
        return new PointsDto(total);
        //SELECT COALESCE(SUM(p.amount), 0) FROM Points p WHERE p.user = :user
        // => 즉 지금 사용자에게 포인트 기록이 있다면 합계, 없다면 0을 주라는 쿼리

    }

    public List<PointsHistoryDto> getPointHistory(Long userId) {
        User user=userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("사용자없음"));
        List<Points> pointList= pointsRepository.findAllByUserOrderByCreatedAtDesc(user);

        return pointList.stream()
                .map(PointsHistoryDto::from)
                .collect(Collectors.toList());
    }
}
