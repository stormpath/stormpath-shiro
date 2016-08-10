package com.stormpath.shiro.spring.config;

import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.cache.MemoryConstrainedCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CacheManagerConfiguration {

    @Bean
    CacheManager getCacheManager() {
        return new MemoryConstrainedCacheManager();
    }
}
