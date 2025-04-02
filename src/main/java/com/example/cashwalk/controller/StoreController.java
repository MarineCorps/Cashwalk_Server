package com.example.cashwalk.controller;

import com.example.cashwalk.dto.StoreItemDto;
import com.example.cashwalk.service.StoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 🎮 스토어 관련 API 요청을 처리하는 컨트롤러
 */
@RestController
@RequestMapping("/api/store")
@RequiredArgsConstructor
public class StoreController {

    private final StoreService storeService;

    /**
     * ✅ 전체 스토어 아이템 목록 조회 API
     * @return StoreItemDto 리스트 (200 OK)
     */
    @GetMapping("/items")
    public ResponseEntity<List<StoreItemDto>> getAllItems() {
        List<StoreItemDto> items = storeService.getAllItems();
        return ResponseEntity.ok(items);
    }
}
