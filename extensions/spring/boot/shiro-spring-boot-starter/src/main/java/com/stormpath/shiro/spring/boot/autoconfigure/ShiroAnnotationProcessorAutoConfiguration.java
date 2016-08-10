package com.stormpath.shiro.spring.boot.autoconfigure;

import com.stormpath.shiro.spring.config.AbstractShiroAnnotationProcessorConfiguration;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

@SuppressWarnings("SpringFacetCodeInspection")
@Configuration
@ConditionalOnProperty(name = "shiro.annotations.enabled", matchIfMissing = true)
public class ShiroAnnotationProcessorAutoConfiguration extends AbstractShiroAnnotationProcessorConfiguration {

    @Bean
    @DependsOn("lifecycleBeanPostProcessor")
    @ConditionalOnMissingBean
    @Override
    public DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator() {
        return super.defaultAdvisorAutoProxyCreator();
    }

    @Bean
    @ConditionalOnMissingBean
    @Override
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(SecurityManager securityManager) {
        return super.authorizationAttributeSourceAdvisor(securityManager);
    }
}
