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
import com.stormpath.shiro.realm.ApplicationRealm;
import com.stormpath.shiro.realm.ApplicationResolver;
import com.stormpath.shiro.realm.DefaultGroupRoleResolver;
import org.apache.shiro.realm.Realm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Set;

/**
 * @since 0.7.0
 */
@SuppressWarnings("SpringFacetCodeInspection")
@Configuration
@ConditionalOnProperty(name = "stormpath.shiro.enabled", matchIfMissing = true)
@AutoConfigureOrder(0)
public class StormpathShiroAutoConfiguration {

    @Autowired
    private Client client;

    @Autowired
    private Application application;
    
    @Value("#{ (@environment['stormpath.shiro.realm.groupRoleResolverModes'] ?: 'HREF').split(',') }")
    protected Set<String> groupRoleResolverModes;

    @Bean(name = "stormpathRealm")
    @ConditionalOnMissingBean
    public Realm getRealm() {
        ApplicationRealm realm = new ApplicationRealm();
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

}
