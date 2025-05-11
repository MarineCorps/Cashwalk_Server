package com.example.cashwalk.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;

@Configuration
public class FirebaseInitializer {

    @PostConstruct // 서버 실행 시 자동 실행
    public void initialize() throws IOException {
        // ✅ classpath 경로에서 JSON 로드 (리소스 폴더 안 firebase 디렉토리)
        ClassPathResource resource = new ClassPathResource("firebase/serviceAccountKey.json");
        try (InputStream serviceAccount = resource.getInputStream()) {

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            // 이미 초기화된 앱이 없다면 Firebase 초기화
            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
            }
        }
    }
}
