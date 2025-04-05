package com.example.cashwalk.service;

import com.example.cashwalk.dto.InviteDto;
import com.example.cashwalk.entity.Invite;
import com.example.cashwalk.entity.User;
import com.example.cashwalk.repository.InviteRepository;
import com.example.cashwalk.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InviteService {

    private final InviteRepository inviteRepository;
    private final UserRepository userRepository;
    private final PointsService pointsService;

    // ✅ 1. 추천 코드 조회 or 생성
    public InviteDto getOrCreateInviteCode(Long userId) {
        User user = findUserById(userId);

        // 이미 추천 코드가 있는 경우 반환
        Optional<Invite> existing = inviteRepository.findByReferrer(user);
        if (existing.isPresent()) {
            return new InviteDto(existing.get().getInviteCode(), false); // 보상 여부는 미사용
        }

        // 추천 코드 새로 생성
        String newCode = generateInviteCode();

        Invite invite = new Invite();
        invite.setReferrer(user);
        invite.setInvitee(null); // ✅ 최초 생성 시 추천받은 사람은 없음
        invite.setInviteCode(newCode);

        inviteRepository.save(invite);
        return new InviteDto(newCode, false);
    }

    // ✅ 2. 추천 코드 적용 (보상 받기)
    public InviteDto applyInviteCode(Long userId, String code) {
        User invitee = findUserById(userId);

        // 자기 자신의 추천 코드는 사용할 수 없음
        Optional<Invite> myInvite = inviteRepository.findByReferrer(invitee);
        if (myInvite.isPresent() && myInvite.get().getInviteCode().equals(code)) {
            throw new IllegalArgumentException("자기 자신의 추천 코드는 사용할 수 없습니다.");
        }

        // 이미 추천 받은 경우
        if (inviteRepository.findByInvitee(invitee).isPresent()) {
            throw new IllegalStateException("이미 추천을 받은 사용자입니다.");
        }

        // 추천 코드 유효성 검증
        Invite invite = inviteRepository.findByInviteCode(code)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 추천 코드입니다."));

        User referrer=invite.getReferrer();
        // ✅ 기존 invite 엔티티에 invitee와 적용 시간만 업데이트
        invite.setInvitee(invitee);
        invite.setAppliedAt(LocalDateTime.now());
        inviteRepository.save(invite);

        pointsService.addInviteReward(referrer,3000,"추천인보상(추천인)");
        pointsService.addInviteReward(invitee,3000,"추천인보상(피추천인)");

        // 💰 포인트 지급은 추후 PointsService와 연동 예정
        return new InviteDto(code, true);
    }

    // ✅ 유틸: 사용자 조회
    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
    }

    // ✅ 유틸: 초대 코드 생성
    private String generateInviteCode() {
        return UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
