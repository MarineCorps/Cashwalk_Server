package com.example.cashwalk.repository;

import com.example.cashwalk.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 📌 Report 엔티티를 위한 JPA Repository
 * Spring Data JPA가 이 인터페이스의 구현체를 자동으로 생성해줌
 */
@Repository // 📌 이 인터페이스가 데이터 액세스 계층임을 나타냄 (스프링 빈으로 등록됨)
public interface ReportRepository extends JpaRepository<Report, Long> {
    // 기본적인 CRUD 메서드는 JpaRepository가 자동으로 제공
}
