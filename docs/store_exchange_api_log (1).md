
# 🛍️ 스토어 아이템 교환 기능 구현 정리 (/api/store/exchange)

**날짜**: 2025-04-03  
**기능 요약**: 사용자가 포인트로 아이템을 교환하는 API를 구현하고, 처리 과정에서 발생한 문제와 해결 방법까지 정리한 문서입니다.

---

## ✅ 구현 목표
- 사용자가 보유한 포인트를 사용하여 스토어 아이템을 교환할 수 있도록 백엔드 API 구현
- 교환 시 포인트 차감, 재고 감소, 교환 내역 로그를 남기는 전체 흐름 구현
- ID 누락 등 사용자 입력 오류에 대한 예외 처리 확인
- 테스트 자동화를 위한 `data.sql` 활용

---

## 🧱 디렉토리별 역할 요약

| 디렉토리 | 설명 |
|----------|------|
| `entity` | DB 테이블과 매핑되는 클래스 정의 (`User`, `StoreItem`, `Points`) |
| `dto` | 클라이언트 요청/응답용 객체 정의 (`StoreItemExchangeRequest`, `StoreItemExchangeResponse`) |
| `repository` | JPA를 통해 DB와 직접 통신 (`UserRepository`, `StoreRepository`, `PointsRepository`) |
| `service` | 비즈니스 로직 처리, 포인트/재고 감소, 예외처리 등 (`StoreService`) |
| `controller` | API 요청 처리 및 응답 (`StoreController`) |

---

## 📄 수정/생성한 파일 목록

- `StoreController.java` (📂 controller)
- `StoreService.java` (📂 service)
- `StoreItemExchangeRequest.java` (📂 dto)
- `StoreItemExchangeResponse.java` (📂 dto)

---

## 🔁 전체 실행 흐름

1. 클라이언트에서 POST `/api/store/exchange` 요청
2. `StoreController`에서 `exchangeItem()` 메서드가 요청을 받음
3. `StoreService.exchangeItem()` 호출
4. 내부 로직
   - 유저 ID로 사용자 조회
   - 아이템 ID로 아이템 조회
   - 포인트가 부족하면 예외 발생
   - 재고가 부족하면 예외 발생
   - 포인트 차감, 재고 감소, 교환 완료 로그 생성
5. DTO 응답으로 성공 메시지 반환

---

## 🔍 주요 코드 설명

### 1. StoreItemExchangeRequest.java
```java
public class StoreItemExchangeRequest {
    private Long userId;
    private Long itemId;
}
```

### 2. StoreItemExchangeResponse.java
```java
public class StoreItemExchangeResponse {
    private String message;

    public StoreItemExchangeResponse(String message) {
        this.message = message;
    }
}
```

### 3. StoreService.java
```java
public StoreItemExchangeResponse exchangeItem(StoreItemExchangeRequest request) {
    Long userId = request.getUserId();
    Long itemId = request.getItemId();

    User user = userRepository.findById(userId)
        .orElseThrow(() -> new RuntimeException("존재하지 않는 사용자입니다."));

    StoreItem item = storeRepository.findById(itemId)
        .orElseThrow(() -> new RuntimeException("존재하지 않는 아이템입니다."));

    if (user.getPoints() < item.getRequiredPoints()) {
        throw new RuntimeException("포인트가 부족합니다.");
    }

    if (item.getStock() <= 0) {
        throw new RuntimeException("재고가 없습니다.");
    }

    user.setPoints(user.getPoints() - item.getRequiredPoints());
    item.setStock(item.getStock() - 1);

    userRepository.save(user);
    storeRepository.save(item);

    return new StoreItemExchangeResponse("아이템 교환이 완료되었습니다.");
}
```

### 4. StoreController.java
```java
@PostMapping("/exchange")
public ResponseEntity<StoreItemExchangeResponse> exchangeItem(
        @RequestBody StoreItemExchangeRequest request) {
    return ResponseEntity.ok(storeService.exchangeItem(request));
}
```

---

## ⚠️ 중간에 발생한 오류

| 문제 | 원인 | 해결 |
|------|------|------|
| `The given id must not be null` | StoreItem ID 값을 `null`로 보냄 | Postman에서 올바른 ID로 수정 (예: 3번 아이템) |
| `Duplicate entry '3'` | `data.sql`로 ID가 3인 아이템 중복 삽입 | MySQL에서 수동 삭제 후 서버 재실행 |
| 데이터 초기화 | 테스트 자동화 위해 `spring.sql.init.mode=always` 사용 | `data.sql` 파일을 수정하여 테스트 데이터 삽입 |

---

## ✅ 테스트 결과 요약

- `POST /api/store/exchange` 요청 성공
- 응답 메시지: `"아이템 교환이 완료되었습니다."`
- DB에서 사용자 포인트 차감, 아이템 재고 감소 확인

---

## 🧠 배운 점 요약

- `@RequestBody`로 받은 값이 `null`일 경우 오류 메시지를 정확히 확인해야 함
- `Entity → DTO → Entity` 사이의 변환 흐름과 책임 분리 학습
- 테스트용 SQL 삽입 자동화로 개발 생산성 향상 가능
- 포인트 차감/재고 감소/예외처리 같은 도메인 로직의 흐름을 명확하게 구성하는 방법 익힘
