/**
 * 📦 StoreItem.java
 * - 스토어에 있는 교환 가능한 아이템을 나타내는 JPA 엔티티 클래스
 * - 상품명, 필요 포인트, 재고 수량을 DB에 저장함
 */

package com.example.cashwalk.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity   //이 클래스가 JPA엔티티이며, DB테이블과 1:1매핑됨
@Table(name="store_item") //테이블 이름설정
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder //객체를 빌더 패턴으로 만들 수 있게해줌
public class StoreItem {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false)
    private String name; //상품명

    @Column(nullable = false)
    private int requiredPoints; //필요한 포인트

    @Column(nullable=false)
    private int stock; //재고 수량
}
