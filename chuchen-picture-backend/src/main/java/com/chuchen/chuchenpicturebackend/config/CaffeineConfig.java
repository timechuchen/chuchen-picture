package com.chuchen.chuchenpicturebackend.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * @author chuchen
 * @description Caffeine 本地缓存配置
 */
@Configuration
public class CaffeineConfig {

    @Bean
    public Cache<String, String> caffeineCache() {
        return Caffeine.newBuilder()
                .initialCapacity(1024)
                .maximumSize(10000L)
                // 缓存 5 分钟移除
                .expireAfterWrite(5L, TimeUnit.MINUTES)
                .build();
    }
}
