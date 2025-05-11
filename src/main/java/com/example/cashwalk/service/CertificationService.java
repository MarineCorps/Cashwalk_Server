package com.example.cashwalk.service;

import com.example.cashwalk.dto.CertificationRequestDto;
import com.example.cashwalk.dto.CertificationResponseDto;
import com.example.cashwalk.entity.User;
import com.example.cashwalk.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDate;

/**
 * ✅ 거주지/활동지 인증 비즈니스 로직 담당 서비스
 * - Controller에서 요청을 받아 실제 인증 관련 작업을 처리한다.
 */
@Service
@RequiredArgsConstructor
public class CertificationService {

    private final UserRepository userRepository;

    /**
     * 🔵 거주지 인증 처리
     * - 사용자의 거주지역과 인증일자를 저장한다.
     * - 30일 이내에는 재인증을 허용하지 않는다.
     */
    @Transactional
    public void certifyResidence(CertificationRequestDto requestDto, Principal principal) {
        User user = getUser(principal); // 현재 로그인한 사용자 가져오기

        // ✅ 이미 거주지 인증 이력이 있다면, 30일 이내 재인증 불가
        if (user.getResidenceCertifiedAt() != null) {
            LocalDate certifiedAt = user.getResidenceCertifiedAt(); // 마지막 인증일자
            LocalDate now = LocalDate.now(); // 오늘 날짜
            if (certifiedAt.plusDays(30).isAfter(now)) { // 30일이 아직 지나지 않았으면
                throw new IllegalStateException("거주지 인증은 30일 후에 다시 할 수 있습니다."); // 인증 거부
            }
        }

        // ✅ 거주지역 정보 업데이트
        user.setResidenceAddress(requestDto.getAddress());

        // ✅ 거주지역 인증 날짜 업데이트
        user.setResidenceCertifiedAt(requestDto.getCertifiedAt());
    }

    /**
     * 🟠 활동지 인증 처리
     * - 사용자의 활동지역과 인증일자를 저장한다.
     * - 30일 이내에는 재인증을 허용하지 않는다.
     */
    @Transactional
    public void certifyActivity(CertificationRequestDto requestDto, Principal principal) {
        User user = getUser(principal); // 현재 로그인한 사용자 가져오기

        // ✅ 이미 활동지 인증 이력이 있다면, 30일 이내 재인증 불가
        if (user.getActivityCertifiedAt() != null) {
            LocalDate certifiedAt = user.getActivityCertifiedAt(); // 마지막 인증일자
            LocalDate now = LocalDate.now(); // 오늘 날짜
            if (certifiedAt.plusDays(30).isAfter(now)) { // 30일이 아직 지나지 않았으면
                throw new IllegalStateException("활동지 인증은 30일 후에 다시 할 수 있습니다."); // 인증 거부
            }
        }

        // ✅ 활동지역 정보 업데이트
        user.setActivityAddress(requestDto.getAddress());

        // ✅ 활동지역 인증 날짜 업데이트
        user.setActivityCertifiedAt(requestDto.getCertifiedAt());
    }

    /**
     * 🟡 사용자 인증정보 조회
     * - 사용자의 거주지/활동지 주소와 인증 완료 날짜를 응답 객체로 반환한다.
     */
    public CertificationResponseDto getCertificationInfo(Principal principal) {
        User user = getUser(principal); // 현재 로그인한 사용자 가져오기

        // ✅ 거주지/활동지 주소 및 인증일자를 DTO로 변환해서 반환
        return new CertificationResponseDto(
                user.getResidenceAddress(),
                user.getResidenceCertifiedAt(),
                user.getActivityAddress(),
                user.getActivityCertifiedAt()
        );
    }

    /**
     * 🔒 현재 로그인한 사용자 정보 조회
     * - Principal 객체에 저장된 이메일을 이용해 User를 조회한다.
     */
    private User getUser(Principal principal) {
        String email = principal.getName(); // JWT 토큰에서 추출한 이메일
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자 정보를 찾을 수 없습니다."));
    }
}
