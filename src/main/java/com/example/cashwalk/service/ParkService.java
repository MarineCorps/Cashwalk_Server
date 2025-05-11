package com.example.cashwalk.service;

import com.example.cashwalk.dto.NearbyParkRequestDto;
import com.example.cashwalk.dto.ParkResponseDto;
import com.example.cashwalk.entity.Park;
import com.example.cashwalk.entity.User;
import com.example.cashwalk.repository.ParkRepository;
import com.example.cashwalk.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ParkService {

    private final ParkRepository parkRepository;
    private final UserRepository userRepository;
    private final PointsService pointsService;

    public List<ParkResponseDto> findNearbyParks(Long userId, NearbyParkRequestDto request) {

        // 🔒 1. 사용자 정보 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("사용자 없음"));

        double lat = request.getLatitude();
        double lng = request.getLongitude();
        double radius = 2000.0; // 검색 반경 (미터)

        // 🎯 2. 오늘 이 사용자가 이미 적립한 공원 ID 리스트
        Set<Long> rewardedParkIds = pointsService.getTodayNwalkParkIds(user);

        // ✅ [디버깅 추가] 오늘 적립한 공원 ID 리스트 출력
        System.out.println("✅ 오늘 적립한 공원 ID 리스트: " + rewardedParkIds);

        // 📦 3. 대략적인 위/경도 박스 범위 계산 (DB 쿼리 효율 위해 1차 필터링용)
        double latDiff = radius / 111320.0;
        double lngDiff = radius / (111320.0 * Math.cos(Math.toRadians(lat)));

        List<Park> bboxParks = parkRepository.findParksInBoundingBox(
                lat - latDiff, lat + latDiff,
                lng - lngDiff, lng + lngDiff
        );

        // ✅ [디버깅 추가] 필터링된 공원 리스트 출력
        System.out.println("✅ Bounding Box 내 필터링된 공원 수: " + bboxParks.size());

        // 🔁 4. 필터링된 공원들을 거리 + 적립 여부 계산 후 DTO로 변환
        List<ParkResponseDto> response = bboxParks.stream()
                .map(park -> {
                    double dist = calculateDistance(lat, lng, park.getLatitude(), park.getLongitude());
                    boolean isInRange = dist <= radius;
                    boolean isRewarded = rewardedParkIds.contains(park.getId());

                    // ✅ [디버깅 추가] 각 공원별 거리와 적립 여부 출력
                    System.out.println("[공원 ID: " + park.getId() + ", 이름: " + park.getName() +
                            ", 거리(m): " + dist + ", 적립여부: " + isRewarded + "]");

                    if (!isInRange) return null;

                    return new ParkResponseDto(
                            park.getId(),
                            park.getName(),
                            park.getLatitude(),
                            park.getLongitude(),
                            dist,
                            isRewarded
                    );
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        // ✅ [디버깅 추가] 최종 반환될 공원 리스트 사이즈
        System.out.println("✅ 최종 반환될 공원 리스트 수: " + response.size());

        return response;
    }

    // 📐 Haversine 공식으로 두 지점 간 거리 계산 (미터 단위)
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        int R = 6371000; // 지구 반지름 (m)
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
}

