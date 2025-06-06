// 사용자 정보 응답(닉네임, 프로필 사진, 포인트 등)
package com.example.cashwalk.dto;

import com.example.cashwalk.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 클라이언트에 사용자 정보를 전달할 때 사용하는 DTO 클래스
 * DTO(Data Transfer Object)는 Entity 대신 외부에 노출할 데이터 구조
 */
@Data // getter, setter 자동 생성
@AllArgsConstructor // 모든 필드를 받는 생성자 자동 생성
@NoArgsConstructor  // 기본 생성자 자동 생성
public class UserDto {

    private Long id;
    private String email;
    private String nickname;
    private int points;
    private String inviteCode;
    private Integer height;
    private Integer weight;
    private String region;
    private String gender;
    private String birthDate;
    private String profileImage;

    /**
     * ✅ User 엔티티 → UserDto 변환 정적 메서드
     */
    public static UserDto from(User user) {
        return new UserDto(
                user.getId(),
                user.getEmail(),
                user.getNickname(),
                user.getPoints(),
                user.getInviteCode(),
                user.getHeight(),
                user.getWeight(),
                user.getRegion(),
                user.getGender(),
                user.getBirthDate() != null ? user.getBirthDate().toString() : null,
                user.getProfileImage()
        );

    }
}
