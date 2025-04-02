//데이터베이스에 직접 접근하는 인터페이스.
//스토어 아이템을 조회하거나 저장하는 기능 담당
package com.example.cashwalk.repository;

import com.example.cashwalk.entity.StoreItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository //이 클래스는 DB와 연결된 Repository라는 의미

public interface StoreRepository extends JpaRepository<StoreItem, Long>{
    //기본적인 CRUD 메서드 들은 JpaRepository가 자동으로 만들어줌
    //create read update delete
}


//인터페이스는 자바의 설계도와 같은 역할
//구현은 SpringDataJpa가 자동으로 해줌
/*
* extends JpaRepository<StoreItem, Long>
storeItem테이블에 대해 id는 long타입이라는뜻
* 이걸로 findAll(),findById,save()등을 자동 생성
* */