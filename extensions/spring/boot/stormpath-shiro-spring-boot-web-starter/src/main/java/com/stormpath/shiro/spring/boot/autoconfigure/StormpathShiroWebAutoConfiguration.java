package com.stormpath.shiro.spring.boot.autoconfigure;

import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.client.Client;
import com.stormpath.sdk.servlet.mvc.WebHandler;
import com.stormpath.shiro.realm.ApplicationRealm;
import com.stormpath.shiro.realm.DefaultGroupRoleResolver;
import com.stormpath.shiro.realm.GroupCustomDataPermissionResolver;
import com.stormpath.shiro.realm.StormpathWebRealm;
import com.stormpath.shiro.servlet.filter.ShiroPrioritizedFilterChainResolver;
import com.stormpath.shiro.servlet.filter.StormpathShiroPassiveLoginFilter;
import com.stormpath.shiro.servlet.mvc.ShiroLoginHandler;
import com.stormpath.shiro.spring.config.web.DefaultShiroFilterChainDefinitionProvider;
import com.stormpath.shiro.spring.config.web.ShiroFilterChainDefinitionProvider;
import org.apache.shiro.config.ConfigurationException;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.util.CollectionUtils;
import org.apache.shiro.web.filter.mgt.FilterChainResolver;
import org.apache.shiro.web.servlet.AbstractShiroFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Filter;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("SpringFacetCodeInspection")
@Configuration
@ConditionalOnProperty(name = "stormpath.shiro.web.enabled", matchIfMissing = true)
public class StormpathShiroWebAutoConfiguration  {

//    @Value("#{ @environment['stormpath.web.login.uri'] ?: '/login' }")
//    private String loginUri;
//
//    @Value("#{ @environment['stormpath.web.login.nextUri'] ?: '/' }")
//    private String loginNextUri;

    @Autowired
    private Client client;

    @Autowired
    private Application application;

    @Bean(name = "stormpathRealm")
    @ConditionalOnMissingBean
    public Realm getRealm() {
        ApplicationRealm realm = new StormpathWebRealm();
        realm.setApplicationRestUrl(application.getHref());
        realm.setClient(client);

        DefaultGroupRoleResolver groupRoleResolver = new DefaultGroupRoleResolver();
        groupRoleResolver.setModes(CollectionUtils.asSet(DefaultGroupRoleResolver.Mode.NAME));
        realm.setGroupRoleResolver(groupRoleResolver);

        GroupCustomDataPermissionResolver groupPermissionResolver = new GroupCustomDataPermissionResolver();
        realm.setGroupPermissionResolver(groupPermissionResolver);

        return realm;
    }

    @Bean
    public WebHandler loginPostHandler() {
        return new ShiroLoginHandler();
    }

    @Bean
    @ConditionalOnMissingBean
    public ShiroFilterChainDefinitionProvider getShiroFilterChainDefinitionProvider() {
        return new DefaultShiroFilterChainDefinitionProvider();
    }


    @Bean
    public ShiroPrioritizedFilterChainResolver shiroPrioritizedFilterChainResolver(
            @Qualifier("filterShiroFilterRegistrationBean") FilterRegistrationBean filterShiroFilterRegistrationBean //) {//,
            ,@Qualifier("stormpathFilter") FilterRegistrationBean stormpathFilter) {

        if (!(filterShiroFilterRegistrationBean.getFilter() instanceof AbstractShiroFilter)) {
            throw new ConfigurationException("Shiro filter registration bean did not contain a AbstractShiroFitler");
        }

        AbstractShiroFilter filter = (AbstractShiroFilter) filterShiroFilterRegistrationBean.getFilter();

        FilterChainResolver originalFilterChainResolver = filter.getFilterChainResolver();


        List<Filter> prioritizedFilters = new ArrayList<>();
        prioritizedFilters.add(stormpathFilter.getFilter());
        stormpathFilter.setEnabled(false);
        prioritizedFilters.add(new StormpathShiroPassiveLoginFilter());
        ShiroPrioritizedFilterChainResolver prioritizedFilterChainResolver = new ShiroPrioritizedFilterChainResolver(originalFilterChainResolver, prioritizedFilters);

        filter.setFilterChainResolver(prioritizedFilterChainResolver);

        return prioritizedFilterChainResolver;
    }



    // TODO: throw a ConfigException, or something better then a raw exception ?
//    @Bean
//    public FilterRegistrationBean getShiroFilterRegistrationBean(SecurityManager securityManager) throws Exception {
//
//        ShiroFilterFactoryBean filterFactoryBean = new ShiroFilterFactoryBean();
//        filterFactoryBean.setSecurityManager(securityManager);
//
//        // TODO: this should not be needed, as stormpath handles the login
//        filterFactoryBean.setLoginUrl(loginUri);
//        filterFactoryBean.setSuccessUrl(loginNextUri);
//        filterFactoryBean.setUnauthorizedUrl(loginUri);
//
////        filterFactoryBean.setFilterChainDefinitionMap(getFilterChainDefinitionMap());
//
//        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
//        filterRegistrationBean.setFilter((AbstractShiroFilter)filterFactoryBean.getObject());
//        filterRegistrationBean.setOrder(1);
//        return filterRegistrationBean;
//    }




}
