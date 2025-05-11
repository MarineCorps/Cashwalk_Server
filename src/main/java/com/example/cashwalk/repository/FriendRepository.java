package com.example.cashwalk.repository;

import com.example.cashwalk.entity.Friend;
import com.example.cashwalk.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FriendRepository extends JpaRepository<Friend, Long> {

    // ✅ 특정 유저가 친구로 등록한 모든 목록 조회
    List<Friend> findByUser(User user);

    // ✅ 특정 친구가 유저를 친구로 등록한 목록 (역방향 확인용)
    List<Friend> findByFriend(User user);

    // ✅ 특정 유저가 특정 상대를 친구로 등록했는지 여부
    Optional<Friend> findByUserAndFriend(User user, User friend);

    // ✅ 특정 유저의 친구 목록 중 닉네임 검색 (내 친구 중 필터링용)
    List<Friend> findByUserAndFriend_NicknameContaining(User user, String nickname);

    // ✅ 친구 관계 삭제용 (정확히 한 방향)
    void deleteByUserAndFriend(User user, User friend);
}
