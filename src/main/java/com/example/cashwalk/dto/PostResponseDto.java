package com.example.cashwalk.dto;

import com.example.cashwalk.entity.Post;
import lombok.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
//@Builder 사용 → 서비스에서 new 없이 .builder() 방식으로 쉽게 객체 생성 가능!
public class PostResponseDto {

    private Long id;
    private String title;
    private String content;
    private String imageUrl;
    private Long userId;
    private LocalDateTime createdAt;
    private String nickname;
    private int likeCount;
    private int commentCount;
    private int views;
    private int bookmarkCount;

    public static PostResponseDto from(Post post, String nickname, int likeCount, int commentCount) {
        return PostResponseDto.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .imageUrl(post.getImageUrl())
                .userId(post.getUserId())
                .createdAt(post.getCreatedAt())
                .nickname(nickname)
                .likeCount(likeCount)
                .commentCount(commentCount)
                .views(post.getViews())
                .bookmarkCount(post.getBookmarkCount())
                .build();
    }

    public static PostResponseDto fromObjectArray(Object[] row) {
        return PostResponseDto.builder()
                .id(((Number) row[0]).longValue())
                .title((String) row[1])
                .content((String) row[2])
                .imageUrl((String) row[3])
                .userId(((Number) row[4]).longValue())
                .createdAt(((Timestamp) row[5]).toLocalDateTime())
                .nickname((String) row[6])
                .likeCount(((Number) row[7]).intValue())
                .commentCount(((Number) row[8]).intValue())
                .views(((Number) row[9]).intValue())
                .bookmarkCount(((Number) row[10]).intValue())
                .build();
    }


}
