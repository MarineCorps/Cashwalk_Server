package com.example.cashwalk.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RunningRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private double distance;

    private Duration duration;

    private double calories;

    private double pace;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    private boolean isDistanceMode;
    private boolean isUnlimited;

    @OneToMany(mappedBy = "runningRecord", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RunningRecordPath> pathList;

    // 러닝 일기 난이도 (1~7), null 허용
    private Integer diaryLevel;

    // 러닝 일기 메모 (선택 입력, null 가능)
    private String diaryMemo;


    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}