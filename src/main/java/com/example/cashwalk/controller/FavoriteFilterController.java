package com.example.cashwalk.controller;

import com.example.cashwalk.entity.BoardType;
import com.example.cashwalk.entity.PostCategory;
import com.example.cashwalk.security.CustomUserDetails;
import com.example.cashwalk.service.FavoriteFilterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/favorites")
@RequiredArgsConstructor
public class FavoriteFilterController {

    private final FavoriteFilterService favoriteFilterService;

    /**
     * 즐겨찾기 추가
     * /api/favorites?boardType=FREE 또는 ?postCategory=BESTLIVE
     */
    @PostMapping
    public ResponseEntity<Map<String, String>> addFavorite(
            @RequestParam(required = false) BoardType boardType,
            @RequestParam(required = false) PostCategory postCategory,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        if (boardType == null && postCategory == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "boardType 또는 postCategory 중 하나는 필요합니다."));
        }

        favoriteFilterService.addFavorite(userDetails.getUserId(), boardType, postCategory);
        return ResponseEntity.ok(Map.of("message", "즐겨찾기 등록 완료"));
    }

    /**
     * 즐겨찾기 해제
     * /api/favorites?boardType=FREE 또는 ?postCategory=BESTLIVE
     */
    @DeleteMapping
    public ResponseEntity<Map<String, String>> removeFavorite(
            @RequestParam(required = false) BoardType boardType,
            @RequestParam(required = false) PostCategory postCategory,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        if (boardType == null && postCategory == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "boardType 또는 postCategory 중 하나는 필요합니다."));
        }

        favoriteFilterService.removeFavorite(userDetails.getUserId(), boardType, postCategory);
        return ResponseEntity.ok(Map.of("message", "즐겨찾기 해제 완료"));
    }

    /**
     * 내가 즐겨찾기한 모든 필터 키 조회 (BoardType + PostCategory 이름 문자열로 반환)
     */
    @GetMapping("/me")
    public ResponseEntity<List<String>> getMyFavorites(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        List<String> favorites = favoriteFilterService.getMyFavoriteFilterKeys(userDetails.getUserId());
        return ResponseEntity.ok(favorites);
    }
}
