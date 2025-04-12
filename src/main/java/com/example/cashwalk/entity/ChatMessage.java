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
public class ChatMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne //여러 메시지가 하나의 채팅방에 속함(N:1)
    private ChatRoom chatRoom;

    @ManyToOne //여러메시지가 하나의 사용자(보낸 사람)에 속함(N:1)
    private User sender;

    private String content;

    private boolean isRead;

    private LocalDateTime createdAt;
}
/*User 1:N ChatMessage (보낸 메시지들)
ChatRoom 1:N ChatMessage (방에 속한 메시지들)
*/