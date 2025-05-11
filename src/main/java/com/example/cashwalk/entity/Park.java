package com.example.cashwalk.entity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;


@Entity
@Getter
@NoArgsConstructor
//name, type, address, latitude, longitude, area, manager
public class Park {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name; //공원이름
    private String type; //공원구분
    private String address; //주소
    private double latitude; //위도
    private double longitude; //경도
    private Double area; //면적  Double은 null 허용
    private String manager; //관리기관
}
