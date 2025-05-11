package com.example.cashwalk.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class InviteStatsDto {
    private String inviteCode;
    private int invitedCount;
    private int invitedMeCount;
    private int totalCash;
}
