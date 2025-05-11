package com.example.cashwalk.controller;

import com.example.cashwalk.dto.GiftResultDto;
import com.example.cashwalk.security.CustomUserDetails;
import com.example.cashwalk.service.GiftService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/gift")
@RequiredArgsConstructor
public class GiftController {

    private final GiftService giftService;

    /**
     * 🎁 친구에게 선물 전송 API
     * @param receiverId 선물 받을 친구의 사용자 ID
     * @param userDetails 로그인된 사용자 정보
     * @return GiftResultDto (보상 결과)
     */
    @PostMapping("/send")
    public GiftResultDto sendGift(
            @RequestParam Long receiverId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        Long senderId = userDetails.getUserId();
        return giftService.sendGift(senderId, receiverId);
    }
}
