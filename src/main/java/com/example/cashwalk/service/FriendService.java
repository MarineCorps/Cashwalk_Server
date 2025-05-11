package com.example.cashwalk.service;
// 필요 import
import com.example.cashwalk.dto.FriendRequestDto;
import com.example.cashwalk.entity.FriendRequest;
import com.example.cashwalk.repository.FriendRequestRepository;
import org.springframework.transaction.annotation.Transactional;
import com.example.cashwalk.entity.Friend;
import com.example.cashwalk.entity.User;
import com.example.cashwalk.repository.FriendRepository;
import com.example.cashwalk.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FriendService {

    private final FriendRepository friendRepository;
    private final UserRepository userRepository;
    private final FriendRequestRepository friendRequestRepository;
    private static final int MAX_FRIEND_COUNT = 100;

    /**
     * ✅ 추천코드로 친구 유저 검색 (자기자신 & 이미 친구인 경우 제외)
     */
    public Optional<User> searchUserByInviteCode(User me, String inviteCode) {
        Optional<User> target = userRepository.findByInviteCode(inviteCode);

        if (target.isEmpty()) return Optional.empty();
        if (target.get().getId().equals(me.getId())) return Optional.empty(); // 자기 자신
        if (friendRepository.findByUserAndFriend(me, target.get()).isPresent()) return Optional.empty(); // 이미 친구

        return target;
    }

    /**
     * ✅ 친구 추가 (쌍방 저장)
     */
    @Transactional
    public boolean addFriendById(User me, Long friendUserId) {
        if (me.getId().equals(friendUserId)) return false; // 자기 자신 추가 방지

        Optional<User> targetOpt = userRepository.findById(friendUserId);
        if (targetOpt.isEmpty()) return false;

        User target = targetOpt.get();

        // 이미 친구인 경우
        if (friendRepository.findByUserAndFriend(me, target).isPresent()) return false;

        // 친구 수 제한
        if (friendRepository.findByUser(me).size() >= MAX_FRIEND_COUNT ||
                friendRepository.findByUser(target).size() >= MAX_FRIEND_COUNT) {
            return false;
        }

        // 쌍방 저장
        friendRepository.save(Friend.builder().user(me).friend(target).build());
        friendRepository.save(Friend.builder().user(target).friend(me).build());

        return true;
    }


    /**
     * ✅ 친구 목록 조회
     */
    public List<Friend> getFriendList(User me, String query) {
        if (query == null || query.trim().isEmpty()) {
            return friendRepository.findByUser(me);
        } else {
            return friendRepository.findByUserAndFriend_NicknameContaining(me, query);
        }
    }


    /**
     * ✅ 친구 삭제 (쌍방 삭제)
     */
    @Transactional
    public void deleteFriend(User me, Long friendId) {
        User friend = userRepository.findById(friendId)
                .orElseThrow(() -> new RuntimeException("해당 친구가 존재하지 않습니다."));

        friendRepository.deleteByUserAndFriend(me, friend);
        friendRepository.deleteByUserAndFriend(friend, me);
    }

    /**
     * ✅ 친구 상태 확인
     */
    public boolean isFriend(User me, User other) {
        return friendRepository.findByUserAndFriend(me, other).isPresent();
    }
    /**
     * 친구 요청 보내기
     */
    @Transactional
    public boolean sendFriendRequest(User sender, Long receiverId) {
        // 본인에게 요청 불가
        if (sender.getId().equals(receiverId)) return false;

        // 요청 받을 유저 존재 여부 확인
        Optional<User> receiverOpt = userRepository.findById(receiverId);
        if (receiverOpt.isEmpty()) return false;

        User receiver = receiverOpt.get();

        // 이미 친구인 경우 차단
        if (isFriend(sender, receiver)) return false;

        // 이미 친구 요청이 있는 경우 중복 차단
        if (friendRequestRepository.findBySenderAndReceiver(sender, receiver).isPresent()) return false;

        // 친구 요청 생성 및 저장
        FriendRequest request = FriendRequest.builder()
                .sender(sender)
                .receiver(receiver)
                .createdAt(LocalDateTime.now())
                .accepted(false)
                .build();

        friendRequestRepository.save(request);
        return true;
    }

    /**
     * 받은 친구 요청 목록 조회
     */
    public List<FriendRequestDto> getReceivedRequests(User me) {
        return friendRequestRepository.findByReceiver(me).stream()
                .map(r -> FriendRequestDto.fromEntity(r, false))
                .collect(Collectors.toList());
    }


    /**
     * 보낸 친구 요청 목록 조회
     */
    public List<FriendRequestDto> getSentRequests(User me) {
        return friendRequestRepository.findBySender(me).stream()
                .map(r -> FriendRequestDto.fromEntity(r, true))
                .collect(Collectors.toList());
    }


    /**
     * 친구 요청 수락
     */
    @Transactional
    public boolean acceptFriendRequest(User receiver, Long senderId) {
        // 요청 보낸 유저 조회
        Optional<User> senderOpt = userRepository.findById(senderId);
        if (senderOpt.isEmpty()) return false;

        User sender = senderOpt.get();

        // 해당 친구 요청 존재 여부 확인
        Optional<FriendRequest> requestOpt = friendRequestRepository.findBySenderAndReceiver(sender, receiver);
        if (requestOpt.isEmpty()) return false;

        // 요청 수락 상태 변경
        FriendRequest request = requestOpt.get();
        request.setAccepted(true);

        // 친구 관계 쌍방 등록
        friendRepository.save(Friend.builder().user(sender).friend(receiver).build());
        friendRepository.save(Friend.builder().user(receiver).friend(sender).build());

        // 요청 삭제
        friendRequestRepository.delete(request);

        return true;
    }

    /**
     * 친구 요청 거절
     */
    @Transactional
    public boolean rejectFriendRequest(User receiver, Long senderId) {
        // 요청 보낸 유저 조회
        Optional<User> senderOpt = userRepository.findById(senderId);
        if (senderOpt.isEmpty()) return false;

        User sender = senderOpt.get();

        // 해당 친구 요청 존재 여부 확인
        Optional<FriendRequest> requestOpt = friendRequestRepository.findBySenderAndReceiver(sender, receiver);
        if (requestOpt.isEmpty()) return false;

        // 요청 삭제
        friendRequestRepository.delete(requestOpt.get());
        return true;
    }

}
