package com.example.cashwalk.service;

import com.example.cashwalk.dto.AuthRequest;
import com.example.cashwalk.dto.AuthResponse;
import com.example.cashwalk.entity.User;
import com.example.cashwalk.repository.UserRepository;
import com.example.cashwalk.security.JwtTokenProvider;
import com.example.cashwalk.utils.PasswordUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;

/**
 * 🔐 인증 관련 서비스 클래스
 * - 일반 로그인, 소셜 로그인(Google/Kakao)
 * - JWT 발급 처리
 */
@Log4j2
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Value("${google.clientId}")
    private String googleClientId;

    public AuthService(UserRepository userRepository, JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    /**
     * ✅ 일반 로그인 (이메일 + 비밀번호)
     */
    public String login(AuthRequest request) {
        Optional<User> userOpt = userRepository.findByEmail(request.getEmail());
        if (userOpt.isEmpty()) return "존재하지 않는 이메일입니다.";

        User user = userOpt.get();
        if (!PasswordUtil.matches(request.getPassword(), user.getPassword())) {
            return "비밀번호가 일치하지 않습니다.";
        }

        return jwtTokenProvider.createToken(user.getEmail(), user.getRole(), user.getId());
    }

    /**
     * ✅ 일반 회원가입
     */
    public String register(AuthRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            return "이미 존재하는 이메일입니다.";
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(PasswordUtil.encodePassword(request.getPassword()));
        user.setNickname("User_" + System.currentTimeMillis());
        user.setRole("ROLE_USER");
        user.setInviteCode(UUID.randomUUID().toString().substring(0, 7).toUpperCase());

        userRepository.save(user);
        return "회원가입 성공!";
    }

    /**
     * ✅ 구글 로그인 처리
     */
    public AuthResponse googleLogin(String idTokenString) {
        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                    GoogleNetHttpTransport.newTrustedTransport(),
                    JacksonFactory.getDefaultInstance()
            ).setAudience(Collections.singletonList(googleClientId)).build();

            GoogleIdToken idToken = verifier.verify(idTokenString);
            if (idToken == null) throw new RuntimeException("유효하지 않은 Google ID Token입니다.");

            String email = idToken.getPayload().getEmail();
            boolean isNewUser = false;
            User user;

            Optional<User> optionalUser = userRepository.findByEmail(email);
            if (optionalUser.isPresent()) {
                user = optionalUser.get();
            } else {
                user = new User();
                user.setEmail(email);
                user.setPassword("");
                user.setNickname("GoogleUser_" + System.currentTimeMillis());
                user.setRole("ROLE_USER");
                user.setInviteCode(UUID.randomUUID().toString().substring(0, 7).toUpperCase());
                user = userRepository.save(user);
                isNewUser = true;
            }

            String jwt = jwtTokenProvider.createToken(user.getEmail(), user.getRole(), user.getId());
            return new AuthResponse(jwt, isNewUser, user.isFirstLoginCompleted());

        } catch (Exception e) {
            throw new RuntimeException("Google 로그인 실패: " + e.getMessage(), e);
        }
    }

    /**
     * ✅ 카카오 로그인 처리
     */
    public AuthResponse kakaoLogin(String accessToken) {
        try {
            // 1. 카카오 사용자 정보 요청
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://kapi.kakao.com/v2/user/me"))
                    .header("Authorization", "Bearer " + accessToken)
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // 2. JSON 파싱하여 이메일 획득
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response.body());
            String kakaoId = root.path("id").asText();
            JsonNode kakaoAccount = root.path("kakao_account");
            String email = kakaoAccount.path("email").asText("kakao_" + kakaoId + "@kakao.local");

            // 3. DB에 사용자 존재 여부 확인
            boolean isNewUser = false;
            User user;

            Optional<User> optionalUser = userRepository.findByEmail(email);
            if (optionalUser.isPresent()) {
                user = optionalUser.get();
            } else {
                user = new User();
                user.setEmail(email);
                user.setPassword("");
                user.setNickname("KakaoUser_" + System.currentTimeMillis());
                user.setRole("ROLE_USER");
                user.setInviteCode(UUID.randomUUID().toString().substring(0, 7).toUpperCase());
                user = userRepository.save(user);
                isNewUser = true;
            }

            // 4. JWT 발급 및 응답 반환
            String jwt = jwtTokenProvider.createToken(user.getEmail(), user.getRole(), user.getId());
            return new AuthResponse(jwt, isNewUser, user.isFirstLoginCompleted());

        } catch (Exception e) {
            throw new RuntimeException("카카오 로그인 실패: " + e.getMessage(), e);
        }
    }
}
