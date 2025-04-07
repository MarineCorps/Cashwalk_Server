package com.example.cashwalk.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration //이 클래스가 설정 클래스임을 Spring에 알려줌
public class RedisConfig {

    // Redis 연결 팩토리 설정 (기본: localhost:6379)
    // Redis 서버 연결 정보를 설정하는 부분
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory("localhost", 6379); // 포트 변경 시 여기를 수정
    }

    // RedisTemplate 설정 (key: String, value: String)
    //Redis와 통신할 수 있는 RedisTemplate 객체 생성. 이 객체를 통해 데이터 저장/조회
    @Bean
    public RedisTemplate<String, String> redisTemplate() {
        RedisTemplate<String, String> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory());
        redisTemplate.setKeySerializer(new StringRedisSerializer()); // key 직렬화 방식
        redisTemplate.setValueSerializer(new StringRedisSerializer()); // value 직렬화 방식
        return redisTemplate;
    }
}
