package com.example.cashwalk.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class CertificationResponseDto {

    private String residenceAddress;
    private LocalDate residenceCertifiedAt;
    private String activityAddress;
    private LocalDate activityCertifiedAt;
}
