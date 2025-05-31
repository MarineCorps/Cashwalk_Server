package com.example.cashwalk.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * 📍 Flutter에서 전송되는 좌표 한 점을 담는 DTO
 */
@Getter
@Setter
public class LatLngDto {

    /**
     * 위도 (Latitude)
     */
    private double lat;

    /**
     * 경도 (Longitude)
     */
    private double lng;
}
