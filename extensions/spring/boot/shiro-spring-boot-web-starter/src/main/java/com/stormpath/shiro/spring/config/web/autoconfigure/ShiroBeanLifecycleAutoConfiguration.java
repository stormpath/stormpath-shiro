package com.stormpath.shiro.spring.config.web.autoconfigure;

import com.stormpath.shiro.spring.config.AbstractShiroBeanLifecycleConfiguration;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(name = "shiro.web.enabled", matchIfMissing = true)
public class ShiroBeanLifecycleAutoConfiguration extends AbstractShiroBeanLifecycleConfiguration {

    @Bean
    public LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
        return super.lifecycleBeanPostProcessor();
    }
}
