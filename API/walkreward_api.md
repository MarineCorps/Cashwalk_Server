# 🚶‍♂️ 동네산책 API

> 공원 위치 기반 포인트 적립, 출석 보상, 누적 통계 등 산책 관련 기능의 API입니다.

---

## 🗺️ 공원 위치 조회

### [GET] /api/park/nearby
- 사용자 위치 기준 반경 내 공원 목록 조회

**Query Params**
- latitude (위도)
- longitude (경도)

---

## 📍 위치 접근 보상

### [POST] /api/points/location
- 사용자가 공원 반경(250m) 내에 진입하면 포인트 적립
- 하루 1회 제한

---

## 🗓️ 출석 이벤트 참여

### [POST] /api/events/checkin
- 산책 이벤트 출석 체크
- 출석 시 보상 지급

---

## 🪙 포인트 이력 조회

### [GET] /api/points/history
- 산책 포함 전체 포인트 적립 내역

---

## 📊 걸음 수 통계

### [GET] /api/steps/history
- 주간/월간 산책 기록 요약

---

## 🔐 공통사항

- 모든 요청은 JWT 인증 필요
- `Authorization: Bearer <token>` 헤더 포함 필수