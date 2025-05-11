package com.example.cashwalk.dto;

import com.example.cashwalk.entity.FriendRequest;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class FriendRequestDto {

    private Long requestId;
    private Long userId;        // 상대 유저의 ID
    private String nickname;    // 상대 유저 닉네임
    private String profileImage;
    private boolean sentByMe;   // true: 내가 보낸 요청, false: 내가 받은 요청
    private LocalDateTime createdAt;

    public static FriendRequestDto fromEntity(FriendRequest request, boolean sentByMe) {
        return FriendRequestDto.builder()
                .requestId(request.getId())
                .userId(sentByMe ? request.getReceiver().getId() : request.getSender().getId())
                .nickname(sentByMe ? request.getReceiver().getNickname() : request.getSender().getNickname())
                .profileImage(sentByMe ? request.getReceiver().getProfileImage() : request.getSender().getProfileImage())
                .sentByMe(sentByMe)
                .createdAt(request.getCreatedAt())
                .build();
    }
}
