package com.example.cashwalk.dto;
//관리자 조회수,좋아요 조작용 dtos
import lombok.*;

@Getter
@Setter
@NoArgsConstructor //기본 생성자
@AllArgsConstructor //전체필드를 받는 생성자

public class ModifyPostStatsRequest {
    private Long postId; //수정할 게시글 ID
    private Integer likeCount; //조작할 좋아요 수
    private Integer viewCount; //조작할 조회수

}
