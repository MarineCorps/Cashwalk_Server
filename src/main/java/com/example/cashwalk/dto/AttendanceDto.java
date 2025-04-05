package com.example.cashwalk.dto;

import com.example.cashwalk.entity.Attendance;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

/**
 * 출석 체크 결과를 클라이언트에 응답할 때 사용하는 DTO
 */
@Data
@AllArgsConstructor
@Builder
public class AttendanceDto {
    private LocalDate date;
    private int reward;

    //Entity ->Dto변환용 static 메서드
    public static AttendanceDto from(Attendance attendance) {
        return AttendanceDto.builder()
                .date(attendance.getDate())
                .reward(attendance.getReward())
                .build();
    }
}
