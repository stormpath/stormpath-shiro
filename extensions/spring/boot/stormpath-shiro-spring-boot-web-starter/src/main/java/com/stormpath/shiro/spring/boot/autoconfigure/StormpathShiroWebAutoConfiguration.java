/*
 * Copyright 2012 Stormpath, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.stormpath.shiro.spring.boot.autoconfigure;

import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.client.Client;
import com.stormpath.sdk.servlet.event.RequestEventListener;
import com.stormpath.shiro.realm.ApplicationRealm;
import com.stormpath.shiro.realm.DefaultGroupRoleResolver;
import com.stormpath.shiro.realm.GroupCustomDataPermissionResolver;
import com.stormpath.shiro.realm.PassthroughApplicationRealm;
import com.stormpath.shiro.servlet.event.LogoutEventListener;
import com.stormpath.shiro.servlet.event.RequestEventListenerBridge;
import com.stormpath.shiro.servlet.filter.ShiroPrioritizedFilterChainResolver;
import com.stormpath.shiro.servlet.filter.StormpathShiroPassiveLoginFilter;
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

/**
 * @since 0.7.0
 */
@SuppressWarnings("SpringFacetCodeInspection")
@Configuration
@ConditionalOnProperty(name = "stormpath.shiro.web.enabled", matchIfMissing = true)
public class StormpathShiroWebAutoConfiguration  {

    @Autowired
    private Client client;

    @Autowired
    private Application application;

    @Bean(name = "stormpathRealm")
    @ConditionalOnMissingBean
    public Realm getRealm() {
        ApplicationRealm realm = new PassthroughApplicationRealm();
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
    @ConditionalOnMissingBean
    public ShiroFilterChainDefinitionProvider getShiroFilterChainDefinitionProvider() {
        return new DefaultShiroFilterChainDefinitionProvider();
    }


    @Bean
    public ShiroPrioritizedFilterChainResolver shiroPrioritizedFilterChainResolver(
            @Qualifier("filterShiroFilterRegistrationBean") FilterRegistrationBean filterShiroFilterRegistrationBean
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

    @Bean
    public RequestEventListener stormpathRequestEventListener() {
        return new RequestEventListenerBridge();
    }

    @Bean
    @ConditionalOnMissingBean
    protected LogoutEventListener logoutEventListener() {
        return new LogoutEventListener();
    }
}
