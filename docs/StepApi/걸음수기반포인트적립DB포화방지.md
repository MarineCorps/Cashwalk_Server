# 걸음 수 기반 포인트 적립 - 하루 최대 100포인트 제한 및 자동 삭제 설계

## 🧠 설계 배경 및 의도

### ✅ 문제 상황
- 캐시워크와 같은 앱은 수만~수십만 유저가 하루에도 수십 개의 걸음 보상을 적립함
- 매번 보상 받을 때마다 `Points` 테이블에 insert 하면 **테이블이 빠르게 비대해짐**
- 하지만 적립 내역을 아예 안 남기면 **보상 이력 추적이 불가능**
- 또한 하루에 최대 적립량 제한 (예: 100포인트)을 두지 않으면 **사용자가 무한 적립 가능**

### ✅ 요구사항 정리
- ✅ 보상 이력은 **하루 안에서는 추적** 가능해야 함
- ✅ 그러나 **하루가 지나면 자동으로 삭제**되어야 함
- ✅ 하루 최대 적립량은 100포인트로 제한
- ✅ DB 부하를 최소화하고 유지보수가 쉬운 구조가 필요

---

## ✅ 선택한 설계 방식

| 문제 | 해결 방안 |
|------|------------|
| 하루 무제한 적립 가능성 | ➝ 하루 적립 포인트 총합을 QueryDSL로 조회하여 제한 |
| `Points` 테이블 무한 증가 | ➝ 하루 지난 `STEP_REWARD` 내역은 @Scheduled로 자동 삭제 |
| 포인트 적립 내역 추적 | ➝ 하루 동안은 기록을 남기되, 장기 저장은 생략 |
| 성능과 유지보수의 균형 | ➝ QueryDSL 사용으로 동적 조건 & 안정성 확보 |

이 설계는 **DB 구조 확장성**, **보상 정책 유연성**, **유저 경험의 간결함**을 동시에 고려한 방식이다.

---

## ✅ API 명세: POST `/api/points/step-reward`
- **요청 DTO**: `StepsDto { int steps; }`
- **처리 로직**:
    - `steps / 100` 으로 환산해 포인트 계산
    - QueryDSL로 당일 누적 적립 포인트를 집계하여, 최대 100까지 허용
    - 가능할 경우 `Points` 테이블에 기록 + `User.totalPoints` 증가

```java
@PostMapping("/step-reward")
public ResponseEntity<String> addStepReward(
        @AuthenticationPrincipal CustomUserDetails userDetails,
        @RequestBody StepsDto request
) {
    User user = userDetails.getUser();
    int requestedPoints = request.getSteps() / 100;
    String result = pointsService.addStepReward(user, requestedPoints);
    return ResponseEntity.ok(result);
}
```

---

## ✅ PointsService 핵심 로직
```java
@Transactional
public String addStepReward(User user, int requested) {
    LocalDateTime start = LocalDate.now().atStartOfDay();
    LocalDateTime end = start.plusDays(1);

    int todayRewarded = pointsRepository.getTodayStepRewardSum(user, start, end);
    int available = Math.min(requested, 100 - todayRewarded);

    if (available <= 0) {
        return "오늘은 더 이상 적립할 수 없습니다 (100포인트 초과)";
    }

    addReward(user, available, PointsType.STEP_REWARD, "걸음수 보상", null);
    return available + "포인트 적립 완료!";
}
```

---

## ✅ QueryDSL Repository 구현
### interface
```java
public interface PointsQuerydslRepository {
    int getTodayStepRewardSum(User user, LocalDateTime start, LocalDateTime end);
}
```

### Impl
```java
public class PointsQuerydslRepositoryImpl implements PointsQuerydslRepository {
    private final JPAQueryFactory queryFactory;

    @Override
    public int getTodayStepRewardSum(User user, LocalDateTime start, LocalDateTime end) {
        QPoints p = QPoints.points;

        Integer sum = queryFactory.select(p.amount.sum())
            .from(p)
            .where(
                p.user.eq(user),
                p.type.eq(PointsType.STEP_REWARD),
                p.createdAt.between(start, end)
            )
            .fetchOne();

        return sum != null ? sum : 0;
    }
}
```

---

## ✅ 하루가 지난 STEP_REWARD 자동 삭제
### PointsRepository
```java
@Modifying
@Query("DELETE FROM Points p WHERE p.type = 'STEP_REWARD' AND p.createdAt < :cutoff")
void deleteOldStepRewards(@Param("cutoff") LocalDateTime cutoff);
```

### PointsService
```java
@Scheduled(cron = "0 0 0 * * *") // 매일 자정 00:00 실행
@Transactional
public void cleanOldStepRewards() {
    LocalDateTime todayStart = LocalDate.now().atStartOfDay();
    pointsRepository.deleteOldStepRewards(todayStart);
    System.out.println("✅ 하루 지난 STEP_REWARD 기록 삭제 완료");
}
```

> ⚠️ 참고: `@EnableScheduling`이 반드시 활성화되어 있어야 `@Scheduled` 동작함

---

## 💡 설계 요약 정리

- ✍️ 기록은 남기되, **지속 보관은 하지 않는다** → 효율적 데이터 운영
- ✍️ 적립 제한은 DB 집계로 **정확하고 안정적**
- ✍️ QueryDSL을 사용해 조건 변경 및 확장에 유연
- ✍️ @Scheduled로 DB 자동 청소 → 실시간 배치 무관하게 안전

이 방식은 실무에서도 성능/유지보수/확장성 측면에서 매우 안정적인 방식이며, 향후 Redis 기반 캐싱 또는 포인트 통계 API와도 자연스럽게 연결될 수 있다.

