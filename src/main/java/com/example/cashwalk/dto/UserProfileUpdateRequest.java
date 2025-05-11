package com.example.cashwalk.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserProfileUpdateRequest {
    private String nickname;
    private String gender;
    private String birthDate; // yyyy-MM-dd 형식 문자열로 받음
    private String region;
    private Integer height;
    private Integer weight;
}
