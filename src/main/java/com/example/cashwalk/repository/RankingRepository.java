package com.example.cashwalk.repository;

import com.example.cashwalk.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RankingRepository extends JpaRepository<User, Long>, RankingRepositoryCustom {
    // 기본적으로 User 조회용 + 커스텀 QueryDSL 동시 지원
}
