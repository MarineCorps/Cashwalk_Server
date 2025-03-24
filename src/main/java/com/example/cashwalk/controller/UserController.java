package com.example.cashwalk.controller;

import com.example.cashwalk.dto.UserDto;
import com.example.cashwalk.service.UserService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * ✅ JWT 인증된 사용자 정보 조회 API
     */
    @GetMapping("/me")
    public UserDto getMyInfo() {
        return userService.getMyInfo();
    }
}
