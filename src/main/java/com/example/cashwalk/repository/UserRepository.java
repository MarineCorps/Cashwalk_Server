package com.example.cashwalk.repository;

import com.example.cashwalk.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * ✅ 사용자 정보 데이터베이스 접근 (JPA Repository)
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * 이메일로 사용자 조회
     * - 일반 로그인 및 소셜 로그인에서 공통적으로 사용
     */
    Optional<User> findByEmail(String email);

    /**
     * 닉네임으로 사용자 존재 여부 확인 (닉네임 중복 체크 용도)
     */
    boolean existsByNickname(String nickname);
    @Query("SELECT u FROM User u WHERE LOWER(u.inviteCode) = LOWER(:inviteCode)")
    Optional<User> findByInviteCode(@Param("inviteCode") String inviteCode);


}
