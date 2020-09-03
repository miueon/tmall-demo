package com.tmallspringboot.demo.config;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.std.StringSerializer;
import com.fasterxml.jackson.databind.util.JSONPObject;
import lombok.val;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.jcache.config.JCacheConfigurerSupport;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.*;
import org.springframework.cache.interceptor.KeyGenerator;

import java.time.Duration;
import java.util.StringJoiner;

@Configuration
@EnableCaching
// @CachingConfig(keyGenerator="keyGenerator")
public class RedisConfig extends CachingConfigurerSupport {

    //    @Bean
//    public RedisCacheManager redisCacheManager(RedisConnectionFactory
//                                                       redisConnectionFactory) {
//
//        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
//                .entryTtl(Duration.ofSeconds(100));
//
//        RedisCacheManager cacheManager = RedisCacheManager.builder(redisConnectionFactory)
//                .cacheDefaults(config).build();
//        return cacheManager;
//    }
    private static final String keyPrefix = "CACHE:";
    @Bean
    public  RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory
                                                                   factory) {
        RedisTemplate<String,Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(factory);
        redisTemplate.setKeySerializer(keySerializer());
        redisTemplate.setHashKeySerializer(keySerializer());
        redisTemplate.setValueSerializer(valueSerializer());
        redisTemplate.setHashValueSerializer(valueSerializer());
        return redisTemplate;
    }

    @Bean
    @Override
    public KeyGenerator keyGenerator() {
        return (target, method, params) ->{
            CharSequence separator;
            // separator =:, prefix, suffix
            StringJoiner joiner = new StringJoiner(":", keyPrefix, "");
            joiner.add(target.getClass().getSimpleName());
            joiner.add(method.getName());

            for (Object param : params) {
                joiner.add(JSONObject.toJSONString(param));
            }
            return joiner.toString();
        };
    }
    /**
     * 管理缓存
     */
    @Bean
    @Primary
    public CacheManager cacheManager(RedisConnectionFactory factory){
        //缓存配置对象
        RedisCacheConfiguration cacheConfig = RedisCacheConfiguration.defaultCacheConfig()
                //设置缓存的默认超时时间：30分钟
                .entryTtl(Duration.ofMinutes(30L))
                //如果是空值，不缓存
                .disableCachingNullValues()
                //设置key序列化器
                .serializeKeysWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(keySerializer()))
                //设置value序列化器
                .serializeValuesWith(RedisSerializationContext.SerializationPair
                        .fromSerializer((valueSerializer())));

        return RedisCacheManager
                .builder(RedisCacheWriter.nonLockingRedisCacheWriter(factory))
                .cacheDefaults(cacheConfig).build();
    }
    /**
     * key序列化
     */
    private RedisSerializer<String> keySerializer() {
        return new StringRedisSerializer();
    }
    /**
     * value序列化
     */
    private RedisSerializer<Object> valueSerializer() {
        return new GenericJackson2JsonRedisSerializer();
    }

}

