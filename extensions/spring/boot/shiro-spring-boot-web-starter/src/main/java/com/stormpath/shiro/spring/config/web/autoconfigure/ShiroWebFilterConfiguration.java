package com.stormpath.shiro.spring.config.web.autoconfigure;

import com.stormpath.shiro.spring.config.web.AbstractShiroWebFilterConfiguration;
import com.stormpath.shiro.spring.config.web.ShiroFilterChainDefinitionProvider;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.servlet.AbstractShiroFilter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(name = "shiro.web.enabled", matchIfMissing = true)
public class ShiroWebFilterConfiguration extends AbstractShiroWebFilterConfiguration {

    @Bean
    @ConditionalOnMissingBean
    protected ShiroFilterFactoryBean shiroFilterFactoryBean(SecurityManager securityManager, ShiroFilterChainDefinitionProvider shiroFilterChainDefinitionProvider) {
        return super.shiroFilterFactoryBean(securityManager, shiroFilterChainDefinitionProvider);
    }

    @Bean
    @ConditionalOnMissingBean
    FilterRegistrationBean filterShiroFilterRegistrationBean(ShiroFilterFactoryBean shiroFilterFactoryBean) throws Exception {

        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        filterRegistrationBean.setFilter((AbstractShiroFilter)shiroFilterFactoryBean.getObject());
        filterRegistrationBean.setOrder(1);

        return filterRegistrationBean;
    }
}
