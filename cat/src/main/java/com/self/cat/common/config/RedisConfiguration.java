package com.self.cat.common.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@Slf4j
public class RedisConfiguration {

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();

        // 1. Set the connection factory first
        // 1. 首先设置连接工厂
        template.setConnectionFactory(factory);

        // 2. Create serializers only once to save memory
        // 2. 序列化器只创建一次以节省内存
        StringRedisSerializer stringSerializer = new StringRedisSerializer();
        GenericJackson2JsonRedisSerializer jsonSerializer = new GenericJackson2JsonRedisSerializer();

        // 3. Set the serializers
        // 3. 设置序列化器
        template.setKeySerializer(stringSerializer);
        template.setValueSerializer(jsonSerializer);

        template.setHashKeySerializer(stringSerializer);
        template.setHashValueSerializer(jsonSerializer);

        return template;
    }
}