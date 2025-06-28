# 🗺️ 동네산책 API 

> 공원 위치 기반 포인트 적립, 출석 보상, 누적 통계 등 산책 관련 기능 API입니다.

---

## 🗺️ 공원 위치 조회

### ✅ \[GET] `/api/park/nearby`

* 사용자 위치 기준 반경 내 공원 목록 조회
* JWT 필요

**Query Params**

* `latitude` (위도)
* `longitude` (경도)

**Response**

```json
[
  {
    "id": 1,
    "name": "청소년공원",
    "address": "충남 천안시 동남구",
    "latitude": 36.81234,
    "longitude": 127.16891,
    "area": 12000.0,
    "manager": "천안시청"
  }
]
```

---

## 📍 위치 접근 보상

### ✅ \[POST] `/api/points/location`

* 사용자가 공원 반경(250m) 내에 진입하면 포인트 적립 (하루 1회 제한)

**Response**

```json
{
  "message": "포인트가 적립되었습니다",
  "earnedPoint": 10
}
```

---

## 🗓️ 출석 이벤트 참여

### ✅ \[POST] `/api/events/checkin`

* 산책 이벤트 출석 체크

**Response**

```json
{
  "message": "출석 완료",
  "reward": 5
}
```

---

## 🪙 포인트 이력 조회

### ✅ \[GET] `/api/points/history`

* 산책 포함 전체 포인트 적립 내역 조회

**Response**

```json
[
  {
    "date": "2025-06-03",
    "type": "STEP_REWARD",
    "point": 20
  },
  {
    "date": "2025-06-03",
    "type": "WALK_REWARD",
    "point": 10
  }
]
```

---

## 📊 걸음 수 통계

### ✅ \[GET] `/api/steps/history`

* 주간/월간 산책 기록 요약

**Response**

```json
{
  "totalSteps": 98000,
  "averageSteps": 14000,
  "mostActiveDay": "2025-06-02"
}
```

---

## 🔐 공통 사항

* 모든 요청은 JWT 인증 필요
* `Authorization: Bearer <token>` 헤더 포함 필수
