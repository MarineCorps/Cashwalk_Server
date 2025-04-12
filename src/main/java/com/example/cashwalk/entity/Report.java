package com.example.cashwalk.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * 사용자의 신고 기록을 저장하는 엔티티 클래스
 */
@Entity // 📌 JPA가 이 클래스를 DB 테이블과 매핑되도록 지정
@Getter // 📌 Lombok - 모든 필드에 대한 getter 메서드 자동 생성
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 📌 JPA 기본 생성자 보호 레벨로 설정
@AllArgsConstructor
@Builder // 📌 객체 생성 시 빌더 패턴 사용 가능
public class Report {

    @Id // 📌 기본 키(primary key) 지정
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 📌 auto_increment 설정
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) // 📌 신고자 - User 테이블과 다대일 관계
    @JoinColumn(name = "user_id") // 📌 외래키 설정
    private User reporter;

    @ManyToOne(fetch = FetchType.LAZY) // 📌 게시글 - 신고 대상이 게시글일 경우
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY) // 📌 댓글 - 신고 대상이 댓글일 경우
    @JoinColumn(name = "comment_id")
    private Comment comment;

    @Enumerated(EnumType.STRING) // 📌 Enum 타입을 문자열로 DB에 저장
    private ReportType type; // 신고 대상 타입: POST 또는 COMMENT

    @Enumerated(EnumType.STRING) // 📌 Enum 타입을 문자열로 저장 (사유 선택 항목)
    @Column(nullable = false)
    private ReportReasonCode reasonCode; // 신고 사유 코드

    @Column(nullable = false)
    private LocalDateTime createdAt; // 신고 시간
}
