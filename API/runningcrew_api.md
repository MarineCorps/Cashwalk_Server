# 🏃‍♂️ 러닝크루 API

> 거리, 시간, 칼로리, 페이스 등 러닝 기록 저장 및 상세 조회 기능입니다.  
> 기록 후 난이도 평가 및 메모 작성이 가능하며, 러닝 데이터 분석 기반 UI 구현에 활용됩니다.

---

## ✅ 러닝 기록 저장

### [POST] /api/running
- 러닝이 끝난 후 데이터를 저장
- 거리, 시간, 페이스, 칼로리 등 포함

**Request Body**
```json
{
  "distance": 3.52,
  "duration": 1231,
  "pace": 4.8,
  "calories": 205,
  "startTime": "2025-05-21T14:00:00",
  "endTime": "2025-05-21T14:25:00",
  "isDistanceMode": true,
  "isUnlimited": false
}
```

---

## 📋 러닝 기록 리스트 조회

### [GET] /api/running/list
- 최근 러닝 기록 카드 형식 리스트 반환
- 일자, 거리, 소요 시간 등 포함

---

## 🔍 러닝 기록 상세 조회

### [GET] /api/running/{id}
- 특정 러닝 기록에 대한 상세 정보 확인
- 메모 및 난이도 평가 포함

---

## 📝 러닝 일지 수정

### [PATCH] /api/running/{id}
- 러닝 후 입력한 난이도(1~7) 및 메모 수정

**Request Body**
```json
{
  "difficulty": 3,
  "memo": "생각보다 쉬웠다!"
}
```

---

## ❌ 러닝 기록 삭제

### [DELETE] /api/running/{id}
- 기록 삭제 요청

---

## 🔐 공통사항

- JWT 인증 필요: `Authorization: Bearer <token>`
- 응답은 JSON 형식