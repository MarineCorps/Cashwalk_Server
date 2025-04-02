//스토어 아이템 조회, 포인트로 교환 처리
package com.example.cashwalk.service;

import com.example.cashwalk.dto.StoreItemDto;
import com.example.cashwalk.entity.StoreItem;
import com.example.cashwalk.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor

public class StoreService {
    private final StoreRepository storeRepository;

    //스토어 아이템전체 목록 조회

    public List<StoreItemDto> getAllItems(){
        //1.모든 아이템을 DB에서 조회
        List<StoreItem> items=storeRepository.findAll();

        //2. StoreItem엔티티를 DTO로 변호나해서 반환
        return items.stream()
                .map(StoreItemDto::from)
                .collect(Collectors.toList());
    }
}
/*
💡 왜 이런 메서드가 필요한가?
우리가 컨트롤러에서 DTO로 응답하려면, JPA Entity 자체를 노출시키지 않고 DTO로 감싸야 해.
그래서 .map(StoreItemDto::from) 같이 변환 과정을 거쳐주는 거야.
* */
