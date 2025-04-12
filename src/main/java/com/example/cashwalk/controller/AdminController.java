package com.example.cashwalk.controller;

import com.example.cashwalk.dto.ModifyPointsRequest;
import com.example.cashwalk.dto.ModifyPostStatsRequest;
import com.example.cashwalk.dto.UserDto;
import com.example.cashwalk.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController // 🧠 REST API를 처리하는 컨트롤러임을 나타냄
@RequestMapping("/api/admin") // 🧠 /api/admin 경로로 시작하는 모든 요청을 처리
@RequiredArgsConstructor // 🧠 final로 선언된 필드를 자동으로 생성자 주입
public class AdminController {

    private final AdminService adminService; // ⚙️ 서비스 계층 호출

    /**
     * ✅ 전체 사용자 목록 조회 API
     * GET /api/admin/users
     */
    @GetMapping("/users")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        List<UserDto> users = adminService.getAllUsers(); // 서비스 호출
        return ResponseEntity.ok(users); // 200 OK + JSON 응답
    }

    /**
     * ✅ 포인트 수동 지급/차감 API
     * POST /api/admin/points
     */
    @PostMapping("/points")
    public ResponseEntity<String> modifyUserPoints(@RequestBody ModifyPointsRequest request) {
        adminService.modifyUserPoints(request); // 서비스 호출
        return ResponseEntity.ok("포인트 조정 완료");
    }

    /**
     * ✅ 특정 사용자의 포인트를 0으로 초기화
     * POST /api/admin/reset-points?userId=1
     */
    @PostMapping("/reset-points")
    public ResponseEntity<String> resetUserPoints(@RequestParam Long userId) {
        adminService.resetUserPoints(userId);
        return ResponseEntity.ok("포인트가 초기화되었습니다.");
    }
    /**
     * ✅ 사용자 삭제 API (관리자 전용)
     * DELETE /api/admin/users/{userId}
     */
    @DeleteMapping("/users/{userId}") //REST방식에 맞춘 삭제 URL
    public ResponseEntity<String> deleteUser(@PathVariable Long userId) {
        adminService.deleteUser(userId);
        return ResponseEntity.ok("사용자가 삭제되었습니다.");
    }
    // ✅ 게시글 좋아요/조회수 조작 API
    @PostMapping("/posts/stats")
    public ResponseEntity<String> modifyPostStats(@RequestBody ModifyPostStatsRequest request) {
        adminService.modifyPostStats(request);
        return ResponseEntity.ok("게시글 통계가 수정되었습니다.");
    }

}
