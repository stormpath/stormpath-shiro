package com.stormpath.shiro.spring.boot.autoconfigure;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableAutoConfiguration
public class StormpathShiroAutoConfigurationTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(StormpathShiroAutoConfigurationTestApplication.class, args);
    }
}
