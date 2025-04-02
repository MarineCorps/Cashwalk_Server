//광고 보상 내역(사용자 ID,광고ID,시청시간,보상 포인트)
package com.example.cashwalk.entity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
//광고 시청 기록을 저장하는 엔티티 클래스

@Entity //이 클래스는 DB테이블로 사용됨
@Table(name="ads")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ads {
    @Id //primalyKey
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //광고를 시청한 사용자
    //User 테이블과 다대일(N:1)관계
    @ManyToOne(fetch=FetchType.LAZY) //여러개의 광고는 한명의 user에게
    @JoinColumn(name="user_id",nullable=false)//외래키 사용
    private User user;

    //광고를 시청한 시간
    @Column(name="watched_at",nullable=false)
    private LocalDateTime watchedAt;

    //지급된 포인트
    @Column(name="reward",nullable=false)
    private int reward;
}
