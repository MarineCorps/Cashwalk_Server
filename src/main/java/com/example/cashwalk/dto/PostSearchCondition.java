package com.example.cashwalk.dto;

import com.example.cashwalk.entity.BoardType;
import lombok.Getter;
import lombok.Setter;

/**
 * 게시글 검색 및 정렬 조건을 담는 DTO
 */
@Getter
@Setter
public class PostSearchCondition {
    private String keyword;         // 검색 키워드 (제목/내용/닉네임 포함 검색)
    private String sort;            // 정렬 기준: like, comment, views, latest
    private int page = 0;           // 페이지 번호
    private int size = 10;          // 페이지 크기
    private BoardType boardType;    // 게시판 종류 필터링 (GENERAL, BESTLIVE, LEGEND 등)
}
