package com.example.cashwalk.repository;

import com.example.cashwalk.entity.RunningRecordPath;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RunningRecordPathRepository extends JpaRepository<RunningRecordPath, Long> {
    // 경로 좌표 저장/조회 기본 기능 제공
}
