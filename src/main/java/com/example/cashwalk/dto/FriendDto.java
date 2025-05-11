package com.example.cashwalk.dto;

import com.example.cashwalk.entity.User;
import lombok.Data;

@Data
public class FriendDto {

    private Long id;
    private String nickname;
    private String profileImage;  // ✅ 추가된 필드
    private String inviteCode;

    public static FriendDto fromUser(User user) {
        FriendDto dto = new FriendDto();
        dto.setId(user.getId());
        dto.setNickname(user.getNickname());
        dto.setProfileImage(user.getProfileImage()); // ✅ 필드 반영
        dto.setInviteCode(user.getInviteCode());
        return dto;
    }
}
