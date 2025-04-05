package com.example.cashwalk.controller;

import com.example.cashwalk.dto.AttendanceDto;
import com.example.cashwalk.entity.User;
import com.example.cashwalk.security.CustomUserDetails;
import com.example.cashwalk.service.EventsService;
import com.example.cashwalk.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventsController {

    private final EventsService eventsService;
    private final UserService userService; // ✅ 추가됨

    // ✅ 출석 체크 API
    @PostMapping("/attendance")
    public ResponseEntity<?> handleAttendance(@AuthenticationPrincipal CustomUserDetails userDetails) {
        User user = userService.findById(userDetails.getUserId());
        AttendanceDto attendanceDto = eventsService.checkAttendance(user);
        return ResponseEntity.ok(attendanceDto); // ✅ 메시지 대신 DTO 응답
    }
}
