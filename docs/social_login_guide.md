
# ✅ 소셜 로그인 연동 전체 정리 (Flutter + Spring Boot)

---

## 📌 공통 준비사항

### 1. SHA-1 키 추출 (Android 디버그 키)
**목적**: 구글, 카카오 개발자 콘솔에 등록해야 함.  
**명령어 (CMD 또는 Git Bash)**:
```bash
keytool -list -v -alias androiddebugkey -keystore "%USERPROFILE%\.android\debug.keystore" -storepass android -keypass android
```
**출력 결과에서 SHA1** 값을 복사하여 Google Cloud Console, Kakao Developers에 등록.

---

## 🟡 [카카오 로그인] - Flutter 쪽

### 1. Kakao SDK 초기화
`AndroidManifest.xml` 설정:
```xml
<meta-data
    android:name="com.kakao.sdk.AppKey"
    android:value="네이티브 앱 키" />
```

### 2. 의존성 추가 (pubspec.yaml)
```yaml
kakao_flutter_sdk_user: ^1.6.1
```

### 3. MainActivity.kt 설정
```kotlin
override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    KakaoSdk.init(this, "네이티브 앱 키")
}
```

### 4. 네이티브 앱 키 vs REST API 키
| 상황 | 사용 키 |
|------|--------|
| Flutter SDK 초기화 | ✅ 네이티브 앱 키 |
| Spring Boot에서 토큰 검증 | ✅ REST API 키 |

---

## 🟢 [Google 로그인] - Flutter 쪽

### 1. Google Developer Console 설정
- OAuth 클라이언트 ID 2개 필요:
  - **Android용**: 패키지명 + SHA-1 키 필요
  - **웹용**: 백엔드에서 ID 토큰 검증 시 사용됨

### 2. 의존성 추가
```yaml
google_sign_in: ^6.1.4
```

### 3. 로그인 코드
```dart
final GoogleSignInAccount? googleUser = await GoogleSignIn().signIn();
final GoogleSignInAuthentication googleAuth = await googleUser!.authentication;
final String idToken = googleAuth.idToken!;
```

---

## 🛠️ [Spring Boot 백엔드]

### 1. 토큰 수신 API
```http
POST /api/auth/google
POST /api/auth/kakao
```

### 2. Google 토큰 검증
```java
GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(httpTransport, jsonFactory)
    .setAudience(Collections.singletonList(CLIENT_ID)) // CLIENT_ID는 웹 클라이언트 ID!
    .build();
```

### 3. Kakao 토큰 검증
```http
GET https://kapi.kakao.com/v2/user/me
Authorization: Bearer {카카오 액세스 토큰}
```

### 4. JWT 발급 후 응답 처리

---

## 🧪 디버깅 팁 모음

| 증상 | 원인 | 해결법 |
|------|------|--------|
| 구글 로그인 idToken null | SHA-1 키 미등록 | SHA-1 Google 등록 |
| 카카오 로그인 서버 응답 없음 | 키 혼동 | Flutter: 네이티브 키 / 서버: REST 키 |
| 403 발생 | Spring Security 설정 누락 | Security 필터/예외 설정 필요 |
| JWT 토큰 없음 | 발급 로직 없음 | 백엔드에 JWT 발급 구현 |

---

## 💾 JWT 저장 (Flutter)
```dart
final prefs = await SharedPreferences.getInstance();
await prefs.setString('jwt', jwt);
```

---

## ✅ 정리 요약

| 구분 | 사용 키 | 주의사항 |
|------|--------|----------|
| Flutter 카카오 SDK | ✅ 네이티브 앱 키 | Manifest에 등록 |
| 서버 카카오 API | ✅ REST API 키 | KakaoAK 인증 방식 |
| Flutter 구글 로그인 | ✅ Android SHA-1 키 | SHA-1 등록 필요 |
| 서버 Google 검증 | ✅ 웹 클라이언트 ID | aud 검증 필수 |
| Spring 인증 통과 | JWT 필터 필요 | Authorization 헤더 필수 |
