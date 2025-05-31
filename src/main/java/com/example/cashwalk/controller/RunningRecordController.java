package com.example.cashwalk.controller;

import com.example.cashwalk.dto.RunningDiaryUpdateDto;
import com.example.cashwalk.dto.RunningRecordDetailDto;
import com.example.cashwalk.dto.RunningRecordRequestDto;
import com.example.cashwalk.dto.RunningRecordResponseDto;
import com.example.cashwalk.entity.User;
import com.example.cashwalk.security.CustomUserDetails;
import com.example.cashwalk.service.RunningRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/running")
public class RunningRecordController {

    private final RunningRecordService runningRecordService;

    // 러닝 기록 저장 API
    @PostMapping("/record")
    public ResponseEntity<?> saveRunningRecord(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody RunningRecordRequestDto requestDto
    ) {
        User user = userDetails.getUser();
        runningRecordService.saveRunningRecord(user, requestDto);

        return ResponseEntity.ok(Map.of(
                "status", 200,
                "message", "러닝 기록 저장 완료"
        ));
    }

    // 러닝 기록 전체 조회 API (카드 리스트)
    @GetMapping("/record")
    public ResponseEntity<?> getMyRunningRecords(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        User user = userDetails.getUser();
        List<RunningRecordResponseDto> records = runningRecordService.getMyRunningRecords(user);

        return ResponseEntity.ok(Map.of(
                "status", 200,
                "message", "러닝 기록 목록 조회 완료",
                "data", records
        ));
    }

    // 러닝 기록 상세 조회 API
    @GetMapping("/record/{id}")
    public ResponseEntity<?> getRunningRecordById(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long id
    ) {
        try {
            User user = userDetails.getUser();
            RunningRecordDetailDto dto = runningRecordService.getRunningRecordById(user, id);

            return ResponseEntity.ok(Map.of(
                    "status", 200,
                    "message", "러닝 기록 상세 조회 완료",
                    "data", dto
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", 400,
                    "message", e.getMessage()
            ));
        }
    }

    // 러닝 일기 수정 API (난이도 + 한줄 메모)
    @PatchMapping("/record/{id}/diary")
    public ResponseEntity<?> updateDiary(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long id,
            @RequestBody RunningDiaryUpdateDto dto
    ) {
        try {
            User user = userDetails.getUser();
            runningRecordService.updateDiary(user, id, dto);

            return ResponseEntity.ok(Map.of(
                    "status", 200,
                    "message", "러닝 일기 수정 완료"
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", 400,
                    "message", e.getMessage()
            ));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRunningRecord(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                    @PathVariable Long id) {
        runningRecordService.deleteRunningRecord(userDetails.getUser().getId(), id);
        return ResponseEntity.noContent().build();
    }

}
