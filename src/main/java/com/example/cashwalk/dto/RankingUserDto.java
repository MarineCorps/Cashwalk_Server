package com.example.cashwalk.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RankingUserDto {
    private Long userId;
    private String nickname;
    private String profileImage;
    private int steps;
    @JsonProperty("isMe")
    private boolean isMe;
    // 나인지 여부 표시
}
