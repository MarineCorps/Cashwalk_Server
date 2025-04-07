
# 🛍️ 스토어 아이템 조회 기능 구현 정리 (`/api/store/items`)

> **날짜**: 2025-04-02  
> **기능 요약**: 사용자가 스토어 아이템 목록을 조회할 수 있도록 백엔드에서 처리한 전체 흐름 정리

---

## ✅ 구현 목표

- 프론트엔드에서 호출할 수 있는 스토어 아이템 전체 조회 API 구현
- DB에서 아이템을 조회하고, DTO로 변환 후 JSON으로 응답
- Spring Boot와 Java 문법을 학습하며 구조 이해

---

## 🧱 디렉토리별 역할 요약

| 디렉토리 | 설명 |
|----------|------|
| `entity` | DB 테이블에 매핑될 클래스 (`StoreItem.java`) |
| `dto` | 프론트에 보낼 데이터 형태 정의 (`StoreItemDto.java`) |
| `repository` | DB와 직접 연결해 데이터를 가져오는 역할 (`StoreRepository.java`) |
| `service` | 비즈니스 로직 담당, Entity → DTO 변환 (`StoreService.java`) |
| `controller` | 클라이언트 요청을 받아 서비스 호출 후 응답 반환 (`StoreController.java`) |

---

## 📄 수정/생성한 파일 목록

- `StoreItem.java` (📂 entity)
- `StoreItemDto.java` (📂 dto)
- `StoreRepository.java` (📂 repository)
- `StoreService.java` (📂 service)
- `StoreController.java` (📂 controller)

---

## 🔁 전체 실행 흐름

1. 클라이언트에서 `/api/store/items`로 GET 요청
2. `StoreController`가 요청을 받음
3. `StoreService.getAllItems()` 호출
4. `StoreRepository.findAll()`로 DB에서 `StoreItem` 리스트 조회
5. Java Stream API로 `StoreItem` → `StoreItemDto` 변환
6. 변환된 DTO 리스트를 JSON으로 반환

---

## 🔍 주요 코드 설명

### 1. `StoreItem.java`

```java
@Entity
@Table(name = "store_item")
public class StoreItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private int requiredPoints;
    private int stock;
}
```

---

### 2. `StoreItemDto.java`

```java
public class StoreItemDto {
    private Long id;
    private String name;
    private int requiredPoints;
    private int stock;

    public static StoreItemDto from(StoreItem entity) {
        StoreItemDto dto = new StoreItemDto();
        dto.id = entity.getId();
        dto.name = entity.getName();
        dto.requiredPoints = entity.getRequiredPoints();
        dto.stock = entity.getStock();
        return dto;
    }
}
```

---

### 3. `StoreRepository.java`

```java
@Repository
public interface StoreRepository extends JpaRepository<StoreItem, Long> {
}
```

---

### 4. `StoreService.java`

```java
@Service
@RequiredArgsConstructor
public class StoreService {
    private final StoreRepository storeRepository;

    public List<StoreItemDto> getAllItems() {
        List<StoreItem> items = storeRepository.findAll();
        return items.stream()
            .map(StoreItemDto::from)
            .collect(Collectors.toList());
    }
}
```

---

### 5. `StoreController.java`

```java
@RestController
@RequestMapping("/api/store")
@RequiredArgsConstructor
public class StoreController {
    private final StoreService storeService;

    @GetMapping("/items")
    public ResponseEntity<List<StoreItemDto>> getItems() {
        return ResponseEntity.ok(storeService.getAllItems());
    }
}
```

---

## ✅ 테스트 결과

- Postman으로 `/api/store/items` 호출
- DB에서 데이터 조회 성공
- 응답: JSON 배열로 스토어 아이템 정상 출력됨

---

## 🧠 배운 점 요약

- Entity ↔ DTO 변환 구조 이해
- Controller → Service → Repository 흐름 숙지
- Spring Boot의 계층적 구조와 책임 분리 방식 학습
