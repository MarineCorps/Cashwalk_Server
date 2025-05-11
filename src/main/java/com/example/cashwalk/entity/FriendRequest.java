package com.example.cashwalk.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FriendRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User sender;  // 요청 보낸 사람

    @ManyToOne
    private User receiver; // 요청 받은 사람

    private LocalDateTime createdAt;

    private boolean accepted;
}
