package com.example.cashwalk.dto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

//스토어 아이템 교환 요청 DTO
// 사용자가 교환할 아이템으 ID를 보내는 역할

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StoreItemExchangeRequest {
    //교환 하려는 아이템의 ID
    private Long userId;
    private Long itemId;
    //Long은 null허용
    //long은 null이 안됨
}
