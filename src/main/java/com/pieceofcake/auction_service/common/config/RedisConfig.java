package com.pieceofcake.auction_service.common.config;

import com.pieceofcake.auction_service.auction.application.sse.AuctionEventService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.*;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

/**
 * Redis 연결 및 RedisTemplate 설정
 */
@Configuration
public class RedisConfig {

    @Value("${spring.data.redis.host}")
    private String redisHost;
    @Value("${spring.data.redis.port}")
    private int redisPort;
    @Value("${spring.data.redis.password}")
    private String redisPassword;

    // SSE를 다중 서버에서도 동작하게 만들기 위한 Redis Pub/Sub 설정
    @Bean
    public ChannelTopic auctionPriceTopic() {
        return new ChannelTopic("auction-price-updates");
    }

    // 직렬화/역직렬화 방식 설정
    @Bean
    public GenericJackson2JsonRedisSerializer genericJsonRedisSerializer() {
        return new GenericJackson2JsonRedisSerializer();
    }

    // 메시지 수신 리스너 등록
    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(
            RedisConnectionFactory connectionFactory,
            AuctionEventService auctionEventService,
            ChannelTopic topic
    ) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(auctionEventService, topic);
        return container;
    }

    /**
     * Redis ConnectionFactory 설정.
     * 기본적으로 localhost:6379에 연결합니다.
     * 필요시 호스트/포트는 application.yml로 외부화하세요.
     */
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration redisConfig = new RedisStandaloneConfiguration();
        redisConfig.setHostName(redisHost);
        redisConfig.setPort(redisPort);
        redisConfig.setPassword(redisPassword);

        // 필요시 비밀번호, 데이터베이스 인덱스 설정
        LettuceClientConfiguration clientConfig = LettuceClientConfiguration.builder()
                .commandTimeout(Duration.ofSeconds(3))
                .build();
        return new LettuceConnectionFactory(redisConfig, clientConfig);
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
    public RedisTemplate<String, Object> redisTemplate(
            RedisConnectionFactory connectionFactory,
            GenericJackson2JsonRedisSerializer genericJsonRedisSerializer
    ) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        // 키를 String으로, 값은 JSON 직렬화 등으로 설정 가능
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(genericJsonRedisSerializer);
        template.setHashValueSerializer(genericJsonRedisSerializer);
        return template;
    }
}
