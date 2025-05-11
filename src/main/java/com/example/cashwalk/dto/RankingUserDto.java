package com.example.cashwalk.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RankingUserDto {
    private Long userId;
    private String nickname;
    private String profileImageUrl;
    private int steps;
    private boolean isMe;  // 나인지 여부 표시
}
