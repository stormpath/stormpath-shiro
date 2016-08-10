package com.stormpath.shiro.spring.config.web;


import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public abstract class ShiroWebFilterConfiguration extends AbstractShiroWebFilterConfiguration {

    @Bean
    protected ShiroFilterFactoryBean shiroFilterFactoryBean(SecurityManager securityManager, ShiroFilterChainDefinitionProvider shiroFilterChainDefinitionProvider) {
        return super.shiroFilterFactoryBean(securityManager, shiroFilterChainDefinitionProvider);
    }
}
