package com.example.cashwalk.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

/**
 * 사용자의 일별 걸음 수 기록을 저장하는 엔티티
 * - 하나의 사용자(User)가 하루에 하나의 기록을 가짐
 * - 누적 걸음 수와 해당 걸음 수에 따른 적립 포인트 저장
 */
@Entity
@Table(name = "steps", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "date"})
}) // user_id + date 조합이 유일하도록 설정 (하루에 한 개의 기록만 허용)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Steps {

    @Id  //이 필드가 PK역할을 함
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    //값을 자동으로 1,2,3증가시켜줌
    //즉 새로 걸음 수를 저장할 때마다 DB에서 자동으로 번호를 매겨줌
    private Long id;

    /**
     * 걸음 수를 기록한 사용자 (User 엔티티와 연관 관계)
     */
    //이 Steps는 하나의 사용자(User)에 속한다는 뜻
    @ManyToOne(fetch = FetchType.LAZY)

    //DB의 user_id라는 컬럼을 외래 키(FK)로 사용
    //nullable=false 무조건 사용자 정보가 있어야만 저장 가능
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * 기록 날짜 (yyyy-MM-dd 형식)
     */
    @Column(name = "date", nullable = false)
    private LocalDate date;

    /**
     * 해당 날짜까지의 누적 걸음 수
     */
    //이 필드는 DB테이블의 steps라는 칼럼으로 저장됨
    // null허용안함
    @Column(name = "steps", nullable = false)
    private int steps;

    /**
     * 오늘까지 적립된 포인트
     */
    @Column(name = "points", nullable = false)
    private int points;

    /**
     * 마지막 업데이트 시각 기록 (선택)
     */
    @Column(name = "last_updated")
    private LocalDate lastUpdated;
}
