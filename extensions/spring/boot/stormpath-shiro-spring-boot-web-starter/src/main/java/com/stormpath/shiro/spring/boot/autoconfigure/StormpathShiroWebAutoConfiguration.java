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
import com.stormpath.shiro.realm.ApplicationResolver;
import com.stormpath.shiro.realm.DefaultGroupRoleResolver;
import com.stormpath.shiro.realm.PassthroughApplicationRealm;
import com.stormpath.shiro.servlet.event.LogoutEventListener;
import com.stormpath.shiro.servlet.event.RequestEventListenerBridge;
import com.stormpath.shiro.servlet.filter.ShiroPrioritizedFilterChainResolver;
import com.stormpath.shiro.servlet.filter.StormpathShiroPassiveLoginFilter;
import org.apache.shiro.config.ConfigurationException;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.spring.config.web.autoconfigure.ShiroWebAutoConfiguration;
import org.apache.shiro.spring.config.web.autoconfigure.ShiroWebFilterConfiguration;
import org.apache.shiro.spring.web.config.DefaultShiroFilterChainDefinition;
import org.apache.shiro.spring.web.config.ShiroFilterChainDefinition;
import org.apache.shiro.web.filter.mgt.DefaultFilter;
import org.apache.shiro.web.filter.mgt.FilterChainResolver;
import org.apache.shiro.web.servlet.AbstractShiroFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Filter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @since 0.7.0
 */
@SuppressWarnings("SpringFacetCodeInspection")
@Configuration
@ImportAutoConfiguration({ShiroWebAutoConfiguration.class, ShiroWebFilterConfiguration.class})
@ConditionalOnProperty(name = "stormpath.shiro.web.enabled", matchIfMissing = true)
public class StormpathShiroWebAutoConfiguration  {

    @Autowired
    private Client client;

    @Autowired
    private Application application;

    @Value("#{ (@environment['stormpath.shiro.realm.groupRoleResolverModes'] ?: 'HREF').split(',') }")
    protected Set<String> groupRoleResolverModes;

    @Bean(name = "stormpathRealm")
    @ConditionalOnMissingBean
    public Realm getRealm() {
        ApplicationRealm realm = new PassthroughApplicationRealm();
        realm.setApplicationRestUrl(application.getHref());
        realm.setClient(client);

        realm.setApplicationResolver(new ApplicationResolver() {
            @Override
            public Application getApplication(Client client, String href) {
                return application;
            }
        });

        DefaultGroupRoleResolver groupRoleResolver = new DefaultGroupRoleResolver();
        groupRoleResolver.setModeNames(groupRoleResolverModes);
        realm.setGroupRoleResolver(groupRoleResolver);

        return realm;
    }

    @Bean
    @ConditionalOnMissingBean
    public ShiroFilterChainDefinition shiroFilterChainDefinition() {
        DefaultShiroFilterChainDefinition filterChainDefinition = new DefaultShiroFilterChainDefinition();
        filterChainDefinition.addPathDefinition("/assets/**", DefaultFilter.anon.name());
        filterChainDefinition.addPathDefinition("/**", DefaultFilter.authc.name());
        return filterChainDefinition;
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
