package com.example.cashwalk.controller;

import com.example.cashwalk.dto.CertificationRequestDto;
import com.example.cashwalk.dto.CertificationResponseDto;
import com.example.cashwalk.service.CertificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

/**
 * ✅ 거주지/활동지 인증 컨트롤러
 */
@RestController
@RequestMapping("/api/certifications")
@RequiredArgsConstructor
public class CertificationController {

    private final CertificationService certificationService;

    /**
     * 🔵 거주지 인증 API
     */
    @PostMapping("/residence")
    public String certifyResidence(@RequestBody CertificationRequestDto requestDto, Principal principal) {
        certificationService.certifyResidence(requestDto, principal);
        return "거주지 인증 완료";
    }

    /**
     * 🟠 활동지 인증 API
     */
    @PostMapping("/activity")
    public String certifyActivity(@RequestBody CertificationRequestDto requestDto, Principal principal) {
        certificationService.certifyActivity(requestDto, principal);
        return "활동지 인증 완료";
    }

    /**
     * 🟡 내 인증정보 조회 API
     */
    @GetMapping
    public CertificationResponseDto getCertificationInfo(Principal principal) {
        return certificationService.getCertificationInfo(principal);
    }
}
