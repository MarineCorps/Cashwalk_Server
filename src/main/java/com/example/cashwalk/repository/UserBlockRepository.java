package com.example.cashwalk.repository;

import com.example.cashwalk.entity.User;
import com.example.cashwalk.entity.UserBlock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 사용자 차단 정보를 저장하고 조회하는 레포지토리
 */
@Repository // 📌 스프링 빈으로 등록되는 데이터 접근 계층
public interface UserBlockRepository extends JpaRepository<UserBlock, Long> {

    // ✅ 내가 차단한 사람들 목록 조회
    List<UserBlock> findByBlocker(User blocker);

    // ✅ 특정 유저를 차단한 적 있는지 확인 (중복 차단 방지용)
    boolean existsByBlockerAndBlocked(User blocker, User blocked);

    // ✅ 차단 해제
    void deleteByBlockerAndBlocked(User blocker, User blocked);

    /**
     * 📌 내가 차단한 사용자들의 ID 목록만 조회
     */
    @Query("SELECT ub.blocked.id FROM UserBlock ub WHERE ub.blocker.id = :blockerId")
    List<Long> findBlockedUserIdsByBlockerId(@Param("blockerId") Long blockerId);

}
