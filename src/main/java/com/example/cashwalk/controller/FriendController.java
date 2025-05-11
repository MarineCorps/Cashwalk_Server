package com.example.cashwalk.controller;

import com.example.cashwalk.dto.FriendDto;
import com.example.cashwalk.entity.Friend;
import com.example.cashwalk.entity.User;
import com.example.cashwalk.security.CustomUserDetails;
import com.example.cashwalk.service.FriendService;
import com.example.cashwalk.service.UserService;
import com.example.cashwalk.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/friends")
@RequiredArgsConstructor
public class FriendController {

    private final FriendService friendService;
    private final UserRepository userRepository;

    /**
     * ✅ 추천 코드로 친구 검색
     */
    // ✅ 추천 코드로 친구 검색 (GET 방식으로 변경됨)
    @GetMapping("/search")
    public ResponseEntity<?> searchByInviteCode(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                @RequestParam String inviteCode) {
        User me = userDetails.getUser();

        Optional<User> result = friendService.searchUserByInviteCode(me, inviteCode);
        if (result.isEmpty()) {
            return ResponseEntity.badRequest().body("유효하지 않거나 이미 친구인 사용자입니다.");
        }

        return ResponseEntity.ok(FriendDto.fromUser(result.get()));
    }


    /**
     * ✅ 친구 추가
     */
    @PostMapping("/add")
    public ResponseEntity<?> addFriend(@AuthenticationPrincipal CustomUserDetails userDetails,
                                       @RequestBody Map<String, Long> request) {
        User me = userDetails.getUser();
        Long friendUserId = request.get("friendUserId");

        boolean success = friendService.addFriendById(me, friendUserId);
        if (!success) {
            return ResponseEntity.badRequest().body("친구 추가 실패");
        }
        return ResponseEntity.ok("친구 추가 성공");
    }


    /**
     * ✅ 친구 목록 조회
     */
    @GetMapping("/list")
    public ResponseEntity<?> getFriends(@AuthenticationPrincipal CustomUserDetails userDetails,
                                        @RequestParam(required = false) String nickname) {
        User me = userDetails.getUser();

        List<Friend> friends = friendService.getFriendList(me, nickname);

        List<FriendDto> friendDtos = friends.stream()
                .map(Friend::getFriend)
                .map(FriendDto::fromUser)
                .collect(Collectors.toList());

        return ResponseEntity.ok(friendDtos);
    }


    /**
     * ✅ 친구 삭제 (끊기)
     */
    @DeleteMapping("/delete/{friendId}")
    public ResponseEntity<?> deleteFriend(@AuthenticationPrincipal User me,
                                          @PathVariable Long friendId) {
        friendService.deleteFriend(me, friendId);
        return ResponseEntity.ok("친구 삭제 완료");
    }

    /**
     * ✅ 친구 상태 확인 API
     * - 내가 이 유저를 친구로 등록했는지 여부 반환
     */
    @GetMapping("/status/{friendId}")
    public ResponseEntity<?> checkFriendStatus(@AuthenticationPrincipal User me,
                                               @PathVariable Long friendId) {
        Optional<User> friendOpt = userRepository.findById(friendId);
        if (friendOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("해당 유저가 존재하지 않습니다.");
        }

        boolean isFriend = friendService.isFriend(me, friendOpt.get());
        return ResponseEntity.ok(isFriend);
    }

    @PostMapping("/requests/send")
    public ResponseEntity<?> sendFriendRequest(@AuthenticationPrincipal CustomUserDetails userDetails,
                                               @RequestBody Map<String, Long> request) {
        Long receiverId = request.get("receiverId");
        boolean success = friendService.sendFriendRequest(userDetails.getUser(), receiverId);
        if (!success) {
            return ResponseEntity.badRequest().body("친구 요청 실패");
        }
        return ResponseEntity.ok("친구 요청 전송 완료");
    }

    @GetMapping("/requests/received")
    public ResponseEntity<?> getReceivedRequests(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(friendService.getReceivedRequests(userDetails.getUser()));
    }

    @GetMapping("/requests/sent")
    public ResponseEntity<?> getSentRequests(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(friendService.getSentRequests(userDetails.getUser()));
    }

    @PostMapping("/requests/accept")
    public ResponseEntity<?> acceptFriendRequest(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                 @RequestBody Map<String, Long> request) {
        Long senderId = request.get("senderId");
        boolean success = friendService.acceptFriendRequest(userDetails.getUser(), senderId);
        if (!success) {
            return ResponseEntity.badRequest().body("요청 수락 실패");
        }
        return ResponseEntity.ok("친구 추가 완료");
    }

    @PostMapping("/requests/reject")
    public ResponseEntity<?> rejectFriendRequest(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                 @RequestBody Map<String, Long> request) {
        Long senderId = request.get("senderId");
        boolean success = friendService.rejectFriendRequest(userDetails.getUser(), senderId);
        if (!success) {
            return ResponseEntity.badRequest().body("요청 거절 실패");
        }
        return ResponseEntity.ok("친구 요청 거절 완료");
    }


}
