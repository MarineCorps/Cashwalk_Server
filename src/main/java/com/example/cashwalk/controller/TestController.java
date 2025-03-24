package com.example.cashwalk.controller;

import com.example.cashwalk.entity.User;
import com.example.cashwalk.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/save")
    public String saveTestUser() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("123456");
        userRepository.save(user);
        return "User 저장 성공!";
    }
}
