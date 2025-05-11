package com.example.cashwalk.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

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

    @Column(name = "message_id", unique = true)
    private String messageId; //중복 방지용 ids

    @ManyToOne //여러 메시지가 하나의 채팅방에 속함(N:1)
    private ChatRoom chatRoom;

    @ManyToOne //여러메시지가 하나의 사용자(보낸 사람)에 속함(N:1)
    private User sender;

    private String content;

    private boolean isRead;

    private LocalDateTime createdAt;

    // 메시지 타입 (TEXT, IMAGE, FILE 등)
    @Enumerated(EnumType.STRING)
    private MessageType type;

    // 이미지 또는 파일 링크 (있으면 저장)
    private String fileUrl;

    public enum MessageType {
        TEXT, IMAGE, FILE,LUCKY_CASH
    }

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        this.messageId = UUID.randomUUID().toString();
    }

}
/*User 1:N ChatMessage (보낸 메시지들)
ChatRoom 1:N ChatMessage (방에 속한 메시지들)
*/