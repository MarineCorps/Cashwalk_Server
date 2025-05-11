package com.example.cashwalk.controller;

import com.example.cashwalk.dto.RankingUserDto;
import com.example.cashwalk.service.RankingService;
import com.example.cashwalk.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ranking")
@RequiredArgsConstructor
public class RankingController {

    private final RankingService rankingService;

    @GetMapping("/daily")
    public List<RankingUserDto> getTodayRanking() {
        // ✅ SecurityContext에서 CustomUserDetails 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = ((CustomUserDetails) authentication.getPrincipal()).getUserId();

        return rankingService.getTodayRanking(userId);
    }
}
