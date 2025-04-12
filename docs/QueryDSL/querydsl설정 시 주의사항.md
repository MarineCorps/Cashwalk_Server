
# QueryDSL 설정 및 빌드 파일 정리

## 1. `build.gradle` 최종 설정

```groovy
plugins {
    id 'java'
    id 'org.springframework.boot' version '3.4.4'
    id 'io.spring.dependency-management' version '1.1.7'
}

group = 'com.example'
version = '0.0.1-SNAPSHOT'

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

repositories {
    mavenCentral()
}

ext {
    querydslDir = "$buildDir/generated/querydsl"
}

configurations {
    compileOnly {
        extendsFrom annotationProcessor
    }
}

dependencies {
    // Spring Boot Starter
    implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
    implementation 'org.springframework.boot:spring-boot-starter-security'
    implementation 'org.springframework.boot:spring-boot-starter-web'
    implementation 'org.springframework.boot:spring-boot-starter-data-redis'

    // Redis
    implementation 'org.apache.commons:commons-pool2'

    // Security
    implementation 'org.springframework.security:spring-security-crypto'

    // JWT
    implementation 'io.jsonwebtoken:jjwt-api:0.11.5'
    implementation 'io.jsonwebtoken:jjwt-impl:0.11.5'
    implementation 'io.jsonwebtoken:jjwt-jackson:0.11.5'

    // Google 로그인
    implementation 'com.google.api-client:google-api-client:1.34.1'
    implementation 'com.google.http-client:google-http-client-jackson2:1.39.2'
    implementation 'com.fasterxml.jackson.core:jackson-databind'

    // MySQL
    runtimeOnly 'com.mysql:mysql-connector-j'

    // ✅ QueryDSL 필수 의존성
    implementation "com.querydsl:querydsl-jpa:5.1.0:jakarta"
    annotationProcessor "com.querydsl:querydsl-apt:5.1.0:jakarta"
    annotationProcessor "jakarta.persistence:jakarta.persistence-api:3.1.0"
    annotationProcessor "jakarta.annotation:jakarta.annotation-api:2.1.1"

    // Lombok
    compileOnly 'org.projectlombok:lombok'
    annotationProcessor 'org.projectlombok:lombok'

    // 테스트
    developmentOnly 'org.springframework.boot:spring-boot-devtools'
    testImplementation 'org.springframework.boot:spring-boot-starter-test'
    testImplementation 'org.springframework.security:spring-security-test'
    testRuntimeOnly 'org.junit.platform:junit-platform-launcher'
}

springBoot {
    mainClass = 'com.example.cashwalk.CashwalkApplication'
}

sourceSets {
    main {
        java {
            srcDirs += [querydslDir]
        }
    }
}

// 👇 QueryDSL 빌드 자동화 Task 설정 추가
tasks.withType(JavaCompile).configureEach {
    options.generatedSourceOutputDirectory = file(querydslDir)
}

clean {
    delete file(querydslDir)
}
```

## 2. IntelliJ에서 "Generated Sources Root" 설정
- **`Generated Sources Root`**는 IntelliJ에서 **`build/generated/querydsl`** 디렉토리를 **소스 코드로 인식**하게 만들어야 합니다.
  - **이 설정이 되어야** IntelliJ에서 **QueryDSL Q파일**을 **자동완성**하거나 **빌드**할 때 문제 없이 인식합니다.

## 3. QueryDSL 설정 설명
- **`querydslDir`**: `buildDir/generated/querydsl`로 설정하여, 빌드 결과로 Q파일을 해당 디렉토리에 생성하도록 했습니다.
- **`annotationProcessor` 설정**: QueryDSL 관련 **`annotationProcessor`**를 추가하여 JPA 관련 Q파일을 자동 생성하도록 했습니다.
- **`clean` Task**: `gradle clean`을 할 때 **`generated`** 디렉토리도 삭제되도록 설정하여, 불필요한 빌드 파일이 남지 않도록 했습니다.
  
## 4. IntelliJ에서 확인
- IntelliJ에서 **`build/generated/querydsl`** 폴더를 우클릭 후 **"Mark Directory as > Generated Sources Root"**로 설정하여, Q파일을 올바르게 인식하도록 해야 합니다.

## 5. 기타 설정 및 참고 사항
- **Gradle 캐시**: Gradle 빌드가 원활하게 동작하려면 캐시 문제를 피해야 하므로, `./gradlew clean build --refresh-dependencies`로 캐시를 갱신할 수 있습니다.
- **JDK 설정**: `toolchain`을 통해 JDK 17 버전을 사용하도록 설정되어 있습니다.
- **Lombok**: `Lombok` 관련 설정도 추가되어 있으며, `annotationProcessor`를 통해 자동으로 처리됩니다.

## 6. QueryDSL Q파일 확인 경로
- 빌드 후, **Q파일**은 `src/main/generated` 폴더 내에 생성됩니다.
- 예: `QUser.java`, `QPost.java` 등이 **`build/generated/querydsl/com/example/cashwalk/entity`** 경로에 생기면 성공입니다.
