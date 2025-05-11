//친구초대
/*📘 코드 설명
📌 @AuthenticationPrincipal
Spring Security에서 JWT 인증된 사용자의 정보를 주입해줌

CustomUserDetails 객체 안에 userId 포함되어 있음

📌 /code API
GET 요청으로 내 추천 코드 조회

이미 존재하면 그대로, 없으면 새로 생성

📌 /apply API
POST 요청으로 추천 코드 적용

@RequestParam으로 쿼리 파라미터 code 받음*/
package com.example.cashwalk.controller;
import com.example.cashwalk.dto.InviteStatsDto;
import com.example.cashwalk.dto.InviteDto;
import com.example.cashwalk.entity.User;
import com.example.cashwalk.security.CustomUserDetails;
import com.example.cashwalk.service.InviteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/invite")
@RequiredArgsConstructor

public class InviteController {
    private final InviteService inviteService;

    //1. 내 추천 코드 조회(없으면 자동 생성)
    @GetMapping("/code")
    public ResponseEntity<InviteDto> getinviteCode(
            @AuthenticationPrincipal CustomUserDetails userDetails){

        Long userId=userDetails.getUserId();
        InviteDto dto=inviteService.getOrCreateInviteCode(userId);
        return ResponseEntity.ok(dto);
    }

    //2. 추천 코드  입력하여 보상받기
    @PostMapping("/apply")
    public ResponseEntity<InviteDto> applyInviteCode(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam String code){
        Long userId=userDetails.getUserId();
        InviteDto dto=inviteService.applyInviteCode(userId,code);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/stats")
    public ResponseEntity<InviteStatsDto> getInviteStats(@AuthenticationPrincipal CustomUserDetails userDetails) {
        User me = userDetails.getUser();
        InviteStatsDto dto = inviteService.getInviteStats(me);
        return ResponseEntity.ok(dto);
    }

}

