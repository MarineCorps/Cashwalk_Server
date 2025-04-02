package com.example.cashwalk.dto;

import java.time.LocalDate;

public class AdHistoryDto {

    private LocalDate createdAt;
    private Long amount;

    // ✅ 이 생성자가 JPQL에서 꼭 필요함!
    public AdHistoryDto(LocalDate createdAt, Long amount) {
        this.createdAt = createdAt;
        this.amount = amount;
    }

    // (선택) Getter/Setter 추가
    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public Long getAmount() {
        return amount;
    }
}
