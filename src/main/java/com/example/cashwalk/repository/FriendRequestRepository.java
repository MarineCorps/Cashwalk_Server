package com.example.cashwalk.repository;

import com.example.cashwalk.entity.FriendRequest;
import com.example.cashwalk.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FriendRequestRepository extends JpaRepository<FriendRequest, Long> {

    List<FriendRequest> findByReceiver(User receiver);
    List<FriendRequest> findBySender(User sender);
    Optional<FriendRequest> findBySenderAndReceiver(User sender, User receiver);
    void deleteBySenderAndReceiver(User sender, User receiver);
}
