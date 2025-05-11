package com.example.cashwalk.entity;
import lombok.*;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name="running_record")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RunningRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //포인트 주인(어떠 사용자에 대한 포인트인지)
    @ManyToOne(fetch = FetchType.LAZY) // 🧠 많은 포인트 기록 → 하나의 사용자
    // DB에서는 user_id라는 이름의 외래키로 저장
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    //러닝을 한 시간
    @Column(name="run_time",nullable=false)
    private LocalDateTime runTime; //몇월 몇칠몇시부터 몇시까지 했는지
    //러닝 거리
    @Column(name="distance",nullable=false)
    private Double distance;
    //소모칼로리
    @Column(name="kcal",nullable = false)
    private Double kcal;
    //평균페이스
    @Column(name="pace",nullable = false)
    private Double pace;

}
