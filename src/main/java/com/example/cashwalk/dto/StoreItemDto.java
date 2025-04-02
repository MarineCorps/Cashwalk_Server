package com.example.cashwalk.dto;

import com.example.cashwalk.entity.StoreItem;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 🛍️ 스토어 아이템 정보를 담는 DTO (클라이언트 응답용)
 */
@Getter
@AllArgsConstructor
public class StoreItemDto {
    private Long id;
    private String name;
    private int requiredPoints;
    private int stock;

    //Entity-> DTO변환 메서드

    public static StoreItemDto from(StoreItem entity) {
        return new StoreItemDto(
                entity.getId(),
                entity.getName(),
                entity.getRequiredPoints(),
                entity.getStock());
    }
    //StoreItem객체에서 각 필드값을 가져옴
}
