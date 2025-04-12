package com.example.cashwalk.controller;

import com.example.cashwalk.dto.ChatMessageDto;
import com.example.cashwalk.dto.ChatRoomSummaryDto;
import com.example.cashwalk.security.CustomUserDetails;
import com.example.cashwalk.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor

/*REST API 전용 –
채팅방 목록, 메시지 목록, 읽음 처리 등 (@RestController)*/
public class ChatController {

    private final ChatService chatService;

    @GetMapping("/rooms")
    public List<ChatRoomSummaryDto> getMyChatRooms(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return chatService.getChatRoomsForUser(userDetails.getUserId());
    }

    @GetMapping("/messages/{roomId}")
    public List<ChatMessageDto> getMessagesForRoom(
            @PathVariable Long roomId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return chatService.getMessagesForRoom(roomId, userDetails.getUserId());
    }
    /*ResponseEntity<?>의 ?는 **Java 제네릭에서 사용하는
    "와일드카드(wildcard)"**이고,
      의미는 "어떤 타입이든 올 수 있다(unknown type)"는 뜻*/

    @PostMapping("/read/{roomId}")
    public ResponseEntity<?> markMessagesAsRead(
            @PathVariable Long roomId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        int updatedCount = chatService.markMessagesAsRead(roomId, userDetails.getUserId());

        return ResponseEntity.ok().body(Map.of(
                "updatedCount", updatedCount,
                "message", "읽음 처리 완료"
        ));
    }


}
