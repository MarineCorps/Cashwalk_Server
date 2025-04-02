//포인트 내역(포인트 ID, 사용자 ID, 변경 유형, 금액,날짜 등)
package com.example.cashwalk.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
//사용자의 포인트 적립/사용 내역을 기록하는 엔티티
//포인트 잔액은 이테이블의 내역을 누적해서계산
//캐시워크처럼 적립/차감 모두 이력으로 남심

@Entity //이 클래스는 DB의 테이블로 매핑됨을 의미함
@Table(name="points") //이름 지정
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Points {
    @Id //이 필드는 테이블의 기본키
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    // 🧠 DB가 자동으로 기본키를 1씩 증가시켜주는 설정
    private Long id;

    //포인트 주인(어떠 사용자에 대한 포인트인지)
    @ManyToOne(fetch = FetchType.LAZY) // 🧠 많은 포인트 기록 → 하나의 사용자
    // DB에서는 user_id라는 이름의 외래키로 저장
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    //변경된 포인트 금액(양수: 적립, 음수:사용)
    private int amount;

    @Enumerated(EnumType.STRING)
    private PointsType type;

    //포인트 변경 시각
    @Column(name="created_at",nullable=false)
    private LocalDateTime createdAt;
}
