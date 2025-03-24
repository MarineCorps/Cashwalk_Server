package com.example.cashwalk.service;

import com.example.cashwalk.dto.AuthRequest;
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
import java.util.Collections;
import java.util.Optional;
import java.util.*;

/**
 * 인증 관련 비즈니스 로직을 처리하는 서비스 클래스
 * - 일반 로그인
 * - 구글 로그인
 * - 카카오 로그인
 */
@Log4j2
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    // 구글 OAuth Client ID (application.properties에서 주입)
    @Value("${google.clientId}")
    private String googleClientId;

    // 생성자 주입 방식 사용
    public AuthService(UserRepository userRepository, JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    /**
     * ✅ 일반 로그인 처리
     * @param request 이메일, 비밀번호
     * @return JWT 토큰 또는 에러 메시지
     */
    public String login(AuthRequest request) {
        Optional<User> userOpt = userRepository.findByEmail(request.getEmail());

        if (userOpt.isEmpty()) {
            return "존재하지 않는 이메일입니다.";
        }

        User user = userOpt.get();
        boolean passwordMatch = PasswordUtil.matches(request.getPassword(), user.getPassword());

        if (!passwordMatch) {
            return "비밀번호가 일치하지 않습니다.";
        }

        return jwtTokenProvider.createToken(user.getEmail(), user.getRole());
    }

    /**
     * ✅ 회원가입 처리
     */
    public String register(AuthRequest request) {
        Optional<User> existingUser = userRepository.findByEmail(request.getEmail());
        if (existingUser.isPresent()) {
            return "이미 존재하는 이메일입니다.";
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(PasswordUtil.encodePassword(request.getPassword()));
        user.setNickname("User_" + System.currentTimeMillis());
        user.setRole("USER");

        userRepository.save(user);
        return "회원가입 성공!";
    }

    /**
     * ✅ 구글 로그인 처리
     * @param idTokenString Flutter에서 받은 Google ID 토큰
     * @return 검증 후 발급된 JWT
     */
    public String googleLogin(String idTokenString) {
        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                    GoogleNetHttpTransport.newTrustedTransport(),
                    JacksonFactory.getDefaultInstance()
            ).setAudience(Collections.singletonList(googleClientId)).build();

            GoogleIdToken idToken = verifier.verify(idTokenString);
            if (idToken != null) {
                GoogleIdToken.Payload payload = idToken.getPayload();
                String email = payload.getEmail();

                User user = userRepository.findByEmail(email).orElseGet(() -> {
                    User newUser = new User();
                    newUser.setEmail(email);
                    newUser.setPassword("");
                    newUser.setNickname("GoogleUser_" + System.currentTimeMillis());
                    newUser.setRole("USER");
                    return userRepository.save(newUser);
                });

                return jwtTokenProvider.createToken(user.getEmail(), user.getRole());
            } else {
                throw new RuntimeException("유효하지 않은 Google ID Token입니다.");
            }

        } catch (Exception e) {
            throw new RuntimeException("Google 로그인 실패: " + e.getMessage(), e);
        }


    }

    /**
     * ✅ 카카오 로그인 처리
     * @param accessToken Flutter에서 받은 카카오 accessToken
     * @return 검증 후 발급된 JWT
     */
    public String kakaoLogin(String accessToken) {
        try {
            // 1. 카카오 사용자 정보 조회
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://kapi.kakao.com/v2/user/me"))
                    .header("Authorization", "Bearer " + accessToken)
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // 2. JSON 파싱
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode root = objectMapper.readTree(response.body());
            String kakaoId = root.path("id").asText();
            JsonNode kakaoAccount = root.path("kakao_account");
            String email = kakaoAccount.path("email").asText("kakao_" + kakaoId + "@kakao.local");

            // 3. 사용자 조회 또는 자동가입
            User user = userRepository.findByEmail(email).orElseGet(() -> {
                User newUser = new User();
                newUser.setEmail(email);
                newUser.setPassword("");
                newUser.setNickname("KakaoUser_" + System.currentTimeMillis());
                newUser.setRole("USER");
                return userRepository.save(newUser);
            });

            return jwtTokenProvider.createToken(user.getEmail(), user.getRole());

        } catch (Exception e) {
            throw new RuntimeException("카카오 로그인 실패: " + e.getMessage(), e);
        }

    }

}
