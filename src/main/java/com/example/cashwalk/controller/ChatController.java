package com.example.cashwalk.controller;

import com.example.cashwalk.dto.*;
import com.example.cashwalk.entity.ChatMessage;
import com.example.cashwalk.entity.LuckyCashHistory;
import com.example.cashwalk.entity.PointsType;
import com.example.cashwalk.security.CustomUserDetails;
import com.example.cashwalk.service.ChatService;
import com.example.cashwalk.service.PointsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;

import com.example.cashwalk.entity.User;
import com.example.cashwalk.repository.*;
import org.springframework.http.HttpStatus;
import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor

/*REST API 전용 –
채팅방 목록, 메시지 목록, 읽음 처리 등 (@RestController)*/
public class ChatController {

    private final ChatService chatService;
    private final UserRepository userRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final PointsService pointsService;
    private final LuckyCashHistoryRepository luckyCashHistoryRepository;

    @GetMapping("/rooms")
    public List<ChatRoomSummaryDto> getMyChatRooms(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return chatService.getChatRoomsForUser(userDetails.getUserId());
    }
    //친구 id를 받아서 기존에 채팅방이있으면 반환 없으면 생성
    @PostMapping("/room")
    public ResponseEntity<?> createOrGetRoom(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody Map<String, Long> request
    ) {
        Long receiverId = request.get("userId");
        Long roomId = chatService.getOrCreateChatRoom(userDetails.getUserId(), receiverId);
        return ResponseEntity.ok(Map.of("roomId", roomId));
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
    //채팅방 숨기기
    @PostMapping("/hide/{roomId}")
    public ResponseEntity<?> hideChatRoom(
            @PathVariable Long roomId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        Long userId = userDetails.getUserId();

        // 숨기기 로직 호출
        chatService.hideChatRoom(userId, roomId);

        return ResponseEntity.ok().body(Map.of(
                "roomId", roomId,
                "message", "채팅방이 숨김 처리되었습니다."
        ));
    }

    @PostMapping("/unhide/{roomId}")
    public ResponseEntity<?> unhideChatRoom(
            @PathVariable Long roomId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        chatService.unhideChatRoom(userDetails.getUserId(), roomId);

        return ResponseEntity.ok().body(Map.of(
                "roomId", roomId,
                "message", "채팅방 숨김이 해제되었습니다."
        ));
    }
    //차단된 친구목록 불러오기
    @GetMapping("/hidden")
    public List<ChatRoomSummaryDto> getHiddenRooms(
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        return chatService.getHiddenChatRoomsForUser(userDetails.getUserId());
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadChatFile(@RequestPart MultipartFile file) {
        try {
            // 저장 경로 설정
            String uploadDir = "uploads/chat/";
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path savePath = Paths.get(uploadDir, fileName);

            // 디렉토리 없으면 생성
            Files.createDirectories(savePath.getParent());

            // 파일 저장
            file.transferTo(savePath);

            // 접근 가능한 URL 반환
            String fileUrl = "/static/chat/" + fileName;

            return ResponseEntity.ok(Map.of(
                    "fileUrl", fileUrl,
                    "message", "파일 업로드 성공"
            ));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "파일 업로드 실패"));
        }
    }

    /*
    @GetMapping("/messages/{roomId}")
    public ResponseEntity<List<ChatMessageResponseDto>> getMessages(
            @PathVariable Long roomId
    ) {
        // 해당 채팅방의 메시지 리스트 조회
        List<ChatMessageResponseDto> messages = chatService.getMessagesByRoom(roomId);

        // JSON 형태로 클라이언트에게 응답
        return ResponseEntity.ok(messages);
    }
    */

    @PostMapping("/lucky-cash/send")
    public ResponseEntity<?> sendLuckyCashMessage(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody ChatRequestDto request
    ) {
        Long senderId = userDetails.getUserId();
        Long receiverId = request.getReceiverId();

        // 유저 객체 가져오기
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new IllegalArgumentException("보낸 사람 없음"));

        // ✅ 관리자(무제한) 아닌 경우 → lucky_cash_history 기준으로 하루 1회 제한
        if (!"ROLE_ADMIN".equals(sender.getRole()))  {
            boolean alreadySent = luckyCashHistoryRepository.existsBySenderIdAndDate(senderId, LocalDate.now());
            if (alreadySent) {
                return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                        .body(Map.of("message", "오늘은 이미 행운캐시를 보냈습니다."));
            }
        }

        // ✅ 채팅방 생성 또는 조회
        Long roomId = chatService.getOrCreateChatRoom(senderId, receiverId);

        // ✅ 메시지 DTO 생성
        ChatMessageDto messageDto = ChatMessageDto.builder()
                .messageId(UUID.randomUUID().toString())
                .type(ChatMessageDto.MessageType.LUCKY_CASH)
                .roomId(String.valueOf(roomId))
                .senderId(senderId)
                .content("🎁 행운캐시가 도착했어요!") // 프론트에서 긁는 버튼으로 처리
                .fileUrl(null)
                .createdAt(LocalDateTime.now())
                .build();

        // ✅ 메시지 전송
        chatService.sendLuckyCashMessage(messageDto, receiverId);

        return ResponseEntity.ok(Map.of(
                "roomId", roomId,
                "message", "행운캐시 전송 완료"
        ));
    }


    @PostMapping("/lucky-cash/redeem")
    public ResponseEntity<?> redeemLuckyCash(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody Map<String, String> request
    ) {
        Long userId = userDetails.getUserId();
        String messageId = request.get("messageId");
        // ✅ 디버깅 로그 추가
        System.out.println("📥 [LuckyCash Redeem 요청 수신] userId: " + userId + ", messageId: " + messageId);
        System.out.println("🔍 [서버 수신] messageId = " + messageId);


        // 🎯 메시지 조회
        ChatMessage message = chatMessageRepository.findByMessageId(messageId)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 메시지입니다."));

        // 🎯 메시지 타입 확인
        if (!message.getType().equals(ChatMessage.MessageType.LUCKY_CASH)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "행운캐시 메시지가 아닙니다."));
        }

        // 🎯 LuckyCashHistory 조회 및 중복 수령 여부 확인
        LuckyCashHistory history = luckyCashHistoryRepository.findByMessageId(message.getId())
                .orElseThrow(() -> new IllegalArgumentException("행운캐시 기록이 존재하지 않습니다."));

        if (!history.getReceiver().getId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "본인이 받은 행운캐시만 열 수 있습니다."));
        }

        if (history.isOpened()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", "이미 오픈한 행운캐시입니다."));
        }

        // 🎯 랜덤 보상 계산
        int reward = new Random().nextInt(10) + 1;

        // 🎯 포인트 적립
        pointsService.addReward(userId, reward, PointsType.LUCKY_CASH, "행운캐시 보상");

        // 🎯 opened = true 저장
        history.setOpened(true);
        luckyCashHistoryRepository.save(history);

        return ResponseEntity.ok(Map.of(
                "reward", reward,
                "message", "🎉 행운캐시를 받았습니다!"
        ));

    }



}
