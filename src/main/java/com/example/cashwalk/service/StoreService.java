package com.example.cashwalk.service;

import com.example.cashwalk.dto.StoreItemDto;
import com.example.cashwalk.dto.StoreItemExchangeRequest;
import com.example.cashwalk.dto.StoreItemExchangeResponse;
import com.example.cashwalk.entity.Points;
import com.example.cashwalk.entity.PointsType;
import com.example.cashwalk.entity.StoreItem;
import com.example.cashwalk.entity.User;
import com.example.cashwalk.repository.PointsRepository;
import com.example.cashwalk.repository.StoreRepository;
import com.example.cashwalk.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 🛒 스토어 관련 서비스 로직 처리
 */
@Service
@RequiredArgsConstructor
public class StoreService {

    private final StoreRepository storeRepository;
    private final UserRepository userRepository;
    private final PointsRepository pointsRepository;

    /**
     * ✅ 모든 스토어 아이템 조회
     * @return StoreItemDto 리스트
     */
    public List<StoreItemDto> getAllItems() {
        List<StoreItem> items = storeRepository.findAll();

        // StoreItem Entity → DTO로 변환하여 반환
        return items.stream()
                .map(StoreItemDto::from)
                .collect(Collectors.toList());
    }

    /**
     * ✅ 아이템 교환 (포인트 차감 + 재고 감소)
     * @param request 교환 요청 (userId, itemId 포함)
     * @return StoreItemExchangeResponse 응답 DTO
     */
    public StoreItemExchangeResponse exchangeItem(StoreItemExchangeRequest request) {
        // 1. 사용자 조회 (예외 처리 포함)
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 2. 아이템 조회
        StoreItem item = storeRepository.findById(request.getItemId())
                .orElseThrow(() -> new IllegalArgumentException("아이템을 찾을 수 없습니다."));

        // 3. 포인트, 재고 체크
        if (user.getPoints() < item.getRequiredPoints()) {
            throw new IllegalStateException("포인트가 부족합니다.");
        }

        if (item.getStock() <= 0) {
            throw new IllegalStateException("재고가 부족합니다.");
        }

        // 4. 포인트 차감 & 아이템 재고 감소
        user.setPoints(user.getPoints() - item.getRequiredPoints());
        item.setStock(item.getStock() - 1);

        // 5. 포인트 사용 내역 기록
        Points pointUse = Points.builder()
                .user(user)
                .amount(-item.getRequiredPoints()) // 포인트 차감은 음수
                .type(PointsType.STORE_USE)
                .createdAt(LocalDateTime.now())
                .build();

        // 6. 저장
        pointsRepository.save(pointUse);
        storeRepository.save(item);
        userRepository.save(user);

        // 7. 응답 DTO 생성 후 반환
        return new StoreItemExchangeResponse(
                item.getId(),
                item.getName(),
                item.getRequiredPoints(),
                user.getPoints() // 현재 남은 포인트
        );
    }
}
