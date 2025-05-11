package com.example.cashwalk.controller;

import com.example.cashwalk.dto.NearbyParkRequestDto;
import com.example.cashwalk.dto.ParkResponseDto;
import com.example.cashwalk.service.ParkService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController //RestApi 명시
@RequestMapping("/api/parks")
@RequiredArgsConstructor //생성자 자동완성
public class ParkController {

    private final ParkService parkService; //서비스 계층 의존성 주입

    /**
     * 사용자의 현재 위치를 받아 반경 250m 이내에 있는 공원 목록을 반환하는 API
     * @param request 위도/경도를 담고 있는 JSON (NearbyParkRequestDto)
     * @return 근처 공원 리스트 (List<Park>)를 JSON 형태로 반환
     */
    @PostMapping("/nearby")
    public List<ParkResponseDto> getNearbyParks(@RequestBody NearbyParkRequestDto request,
                                                @RequestAttribute("userId") Long userId) {
        // 🔐 JWT 필터에서 저장한 userId를 활용
        return parkService.findNearbyParks(userId, request);
    }
}
