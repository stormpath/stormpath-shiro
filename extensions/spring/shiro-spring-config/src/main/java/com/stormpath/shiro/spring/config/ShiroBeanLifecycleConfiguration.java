package com.stormpath.shiro.spring.config;

import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ShiroBeanLifecycleConfiguration extends AbstractShiroBeanLifecycleConfiguration {

    @Bean
    public LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
        return super.lifecycleBeanPostProcessor();
    }
}
