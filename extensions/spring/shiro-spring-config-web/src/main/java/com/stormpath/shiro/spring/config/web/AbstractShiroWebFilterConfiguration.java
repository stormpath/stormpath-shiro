package com.stormpath.shiro.spring.config.web;


import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;

public abstract class AbstractShiroWebFilterConfiguration {

    protected ShiroFilterFactoryBean shiroFilterFactoryBean(SecurityManager securityManager, ShiroFilterChainDefinitionProvider shiroFilterChainDefinitionProvider) {

        ShiroFilterFactoryBean filterFactoryBean = new ShiroFilterFactoryBean();
        filterFactoryBean.setSecurityManager(securityManager);
        filterFactoryBean.setFilterChainDefinitionMap(shiroFilterChainDefinitionProvider.getFilterChainDefinition());

        return filterFactoryBean;
    }
}
