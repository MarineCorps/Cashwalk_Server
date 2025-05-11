package com.example.cashwalk.dto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class CertificationRequestDto {
    private String address;  //주소
    private LocalDate certifiedAt; //인증한 날짜
}
