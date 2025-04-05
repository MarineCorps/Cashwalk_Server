//출석 기록을 저장하는 DB테이블
package com.example.cashwalk.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
//사용자의 출석 체크 기록을 저장하는 엔티티
@Entity
@Table(name="attendacne", uniqueConstraints = @UniqueConstraint(columnNames={"user_id","date"}))
//같은 날 두 번 출석 못하도록 제약 설정
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Attendance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //출석한 사용자
    @ManyToOne(fetch=FetchType.LAZY) //여러 춯석기록->하나의 사용자
    @JoinColumn(name="user_id",nullable=false)
    private User user;

    //출석날짜
    @Column(nullable=false)
    private LocalDate date;

    //출석 보상 포인트
    @Column(name="reward",nullable=false)
    private int reward;
}
