package com.pieceofcake.auction_service.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.*;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Redis 연결 및 RedisTemplate 설정
 */
@Configuration
public class RedisConfig {

    /**
     * Redis ConnectionFactory 설정.
     * 기본적으로 localhost:6379에 연결합니다.
     * 필요시 호스트/포트는 application.yml로 외부화하세요.
     */
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        // 기본 설정: localhost:<port>
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        config.setHostName("localhost");
        config.setPort(6379);
        // 필요시 비밀번호, 데이터베이스 인덱스 설정
        // config.setPassword(RedisPassword.of("yourPassword"));
        // config.setDatabase(1);
        return new LettuceConnectionFactory(config);
    }

    /**
     * 문자열 기반 RedisTemplate
     * 키/값을 모두 String으로 사용할 때 편리합니다.
     */
    @Bean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory connectionFactory) {
        return new StringRedisTemplate(connectionFactory);
    }

    /**
     * 범용 RedisTemplate (객체 직렬화 등)
     * 필요에 따라 사용하세요.
     */
    @Bean
    public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<Object, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        // 키를 String으로, 값은 JSON 직렬화 등으로 설정 가능
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        return template;
    }
}
