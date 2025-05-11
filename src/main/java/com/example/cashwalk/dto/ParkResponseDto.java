package com.example.cashwalk.dto;
import lombok.Getter;
import lombok.AllArgsConstructor;
import com.example.cashwalk.entity.Park;

@Getter
@AllArgsConstructor
public class ParkResponseDto {
    private Long id;
    private String parkName;
    private double latitude;
    private double longitude;
    private double distance;
    private boolean isRewardedToday; //오늘 적립했는지 안했는지
}
