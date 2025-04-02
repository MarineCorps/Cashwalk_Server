//광고보상
package com.example.cashwalk.controller;

import com.example.cashwalk.dto.AdsDto;
import com.example.cashwalk.dto.AdHistoryDto;
import com.example.cashwalk.dto.AdsRewardHistoryDto;
import com.example.cashwalk.security.CustomUserDetails;
import com.example.cashwalk.service.AdsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//광고 시청 완료 보상 API를 처리하는 컨트롤러

@RestController //이 클래스를 REST API응답용 컨트롤러임
@RequestMapping("/api/ads")// 모든 경로는 /api/ads/로 시작
@RequiredArgsConstructor //생성자 자동 생성(final 필드

public class AdsController {

    private final AdsService adsService;
    //광고 시청 완료 후 보상 포인트를 지급한다 API
    // JWT 인증이 되어야 접근 가능
    // 응답: {reward :10}
    /*** ✅ 광고 시청 완료 시 포인트 지급*/

    @PostMapping("/reward")
    public ResponseEntity<AdsDto> rewardAd(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ){
        AdsDto response =adsService.rewardForAd(userDetails.getUserId());
        return ResponseEntity.ok(response);
    }
    //광고 보상 내역 조회(최신순)
    //GET: /api/ads/history
    //응답 : List<AdsRewardHistoryDto>
    @GetMapping("/history")
    public ResponseEntity<List<AdsRewardHistoryDto>> getAdHistory(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ){
        List<AdsRewardHistoryDto> history=adsService.getAdRewardHistory(userDetails.getUserId());
        return ResponseEntity.ok(history);
    }

    @GetMapping("/history/summary")
    public ResponseEntity<List<AdHistoryDto>> getAdHistorySummary(@AuthenticationPrincipal CustomUserDetails userDetails){
        Long userId=userDetails.getUserId();
        List<AdHistoryDto> summary=adsService.getAdRewardDailySummary(userId);
        return ResponseEntity.ok(summary);
    }
}
