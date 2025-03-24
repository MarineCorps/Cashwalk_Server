
# 🚀 Google & Kakao 로그인 통합 구현 (Flutter + Spring Boot)

Flutter와 Spring Boot를 기반으로 구글/카카오 로그인을 구현하고 JWT 인증까지 연동하는 전 과정을 상세히 기록한 문서입니다.  
✅ **Notion에 바로 붙여넣을 수 있도록 마크다운 포맷으로 정리했습니다.**

---

## 0️⃣ 시작 전 준비

### 🔧 사용 스택
- **Flutter (Android only)**
- **Spring Boot (Java 17)**
- **Google OAuth**
- **Kakao SDK**
- **MySQL + JPA**
- **JWT 인증 방식**

---

## 1️⃣ Flutter 프로젝트 기본 셋업

1. Android 프로젝트 생성 (`flutter create cashwalk`)
2. Android 패키지명 확인 및 설정
3. `google-services.json`, `kakao SDK`, `gradle` 관련 설정

---

## 2️⃣ 카카오 로그인 구현

### ✅ 주요 단계
- [x] Kakao Developers 앱 생성
- [x] Android 네이티브 앱 키 확인
- [x] `flutter pub add kakao_flutter_sdk_user`
- [x] `AndroidManifest.xml`에 인터넷 권한, Kakao 설정 추가
- [x] `KakaoSdk.init(...)` 호출

### 🧪 테스트
- 에뮬레이터에서도 로그인 가능 (단, `KakaoAccount` 로그인으로 유도됨)

---

## 3️⃣ 구글 로그인 구현

### 📁 Firebase Console 작업
- [x] 새 프로젝트 생성 (예: cashwalk)
- [x] Android 앱 등록 (패키지명 정확히 입력)
- [x] `SHA1` 등록 (debug.keystore, `./gradlew signingReport`)
- [x] `google-services.json` 다운로드 후 `android/app`에 위치
- [x] OAuth 클라이언트 ID 확인

### 🧩 pubspec.yaml 의존성
```yaml
google_sign_in: ^6.1.6
```

### ⚙ gradle 설정

**`android/build.gradle`**
```groovy
buildscript {
    dependencies {
        classpath 'com.google.gms:google-services:4.4.0'
    }
    repositories {
        google()
        mavenCentral()
    }
}
```

**`android/app/build.gradle`**
```groovy
plugins {
    id "com.google.gms.google-services"
}
```

---

## 4️⃣ 백엔드 Spring Boot 연동 (Google ID Token 검증)

### 🔐 백엔드 처리 흐름
1. Flutter → idToken 전송 (POST `/api/auth/google`)
2. Spring Boot → Google API로 ID 토큰 검증
3. 사용자 정보 추출 후 회원가입 or JWT 발급
4. JWT를 Flutter에 응답

### 🔎 주요 주의사항
- **Google ID 토큰 검증 시 `web client ID` 사용해야 함!**
- `GoogleIdTokenVerifier.Builder(transport, jsonFactory)` 생성 시, 대상 audience에 **웹 클라이언트 ID**를 넣어야 함

---

## 5️⃣ 자주 발생하는 오류 & 해결법

| 오류 | 원인 | 해결법 |
|------|------|--------|
| `sign_in_failed ApiException: 10` | SHA1 mismatch | Firebase에 SHA1 다시 등록 후 앱 재빌드 |
| `403 Forbidden` | 백엔드에서 토큰 검증 실패 | 웹 클라이언트 ID 확인 필요 |
| `signingReport 안 됨` | gradle 설정 문제 | 루트 `build.gradle`에서 plugins 블록보다 `buildscript`가 먼저 와야 함 |

---

## 6️⃣ 키 등록 실수 방지 체크리스트

- [ ] Firebase에 등록한 **SHA1**과 로컬 키(`debug.keystore`) 일치 확인
- [ ] OAuth 클라이언트 ID는 **웹 클라이언트**용과 **Android용**을 구분
- [ ] `google-services.json`을 `android/app/` 경로에 정확히 위치
- [ ] `build.gradle`에 google-services 플러그인 적용 순서 맞는지 확인
- [ ] Flutter 실행 전 `flutter clean && flutter pub get`

---

## 7️⃣ 마무리

### ✅ 최종 확인 사항
- Google 로그인 → 정상 ID Token 발급됨
- 백엔드에서 토큰 검증 후 JWT 발급 → 프론트 저장 완료
- JWT로 `/api/users/me` 등 인증 API 호출 가능
- Kakao 로그인도 동일하게 동작

### 🗂 추천 파일 위치
- 이 문서는 `/docs/Google_Kakao_Login_Flutter_SpringBoot.md`로 저장
- Git으로 프로젝트를 관리한다면 문서도 함께 관리 가능

---

📌 **문서 최신화 필요 시 Notion에서 복붙 → 수정 후 다시 md로 내보내면 OK!**
