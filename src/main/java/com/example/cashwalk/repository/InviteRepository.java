//친구 초대 정보 저장 및 보상 처리
package com.example.cashwalk.repository;

import com.example.cashwalk.entity.Invite;
import com.example.cashwalk.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
public interface InviteRepository extends JpaRepository<Invite, Long> {
    // 피추천인 기준으로 조회
    // 이미 추천을 받은 적 있는지 확인
    Optional<Invite> findByInvitee(User invitee);

    // 추천 코드로 추천인 정보조회
    // 입력된 코드로 추천인 찾기
    Optional<Invite> findByInviteCode(String code);

    // 추천인 기준으로 기존 코드 조회
    //로그읺나 사용자의 기존 추천 코드 조회용
    Optional<Invite> findByReferrer(User referer);
}
