package com.example.cashwalk.repository;

import com.example.cashwalk.entity.RunningRecord;
import com.example.cashwalk.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RunningRecordRepository extends JpaRepository<RunningRecord, Long> {

    // 로그인한 사용자의 전체 러닝 기록 조회
    List<RunningRecord> findByUser(User user);

    // 특정 사용자 소유의 러닝 기록 1건 조회
    Optional<RunningRecord> findByIdAndUser(Long id, User user);
}
