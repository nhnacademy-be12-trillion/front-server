package com.nhnacademy.frontserver.infra;

import com.nhnacademy.frontserver.layout.cartbadge.listener.CartBadgeCacheEvictListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

@Configuration
public class RedisListenerConfig {

    public static final String CART_BADGE_TOPIC = "cart-badge-evict-topic";

    // [NEW] app.cluster.enabled가 'true'일 때만 빈 등록
    // false일 경우 컨테이너 자체가 뜨지 않아 Redis Connection 및 Thread 낭비를 막음
    @Bean
    @ConditionalOnProperty(name = "app.cluster.enabled", havingValue = "true")
    RedisMessageListenerContainer redisContainer(RedisConnectionFactory connectionFactory,
                                                 CartBadgeCacheEvictListener listener) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        // 토픽(채널)과 리스너 연결
        container.addMessageListener(listener, new ChannelTopic(CART_BADGE_TOPIC));
        return container;
    }
}