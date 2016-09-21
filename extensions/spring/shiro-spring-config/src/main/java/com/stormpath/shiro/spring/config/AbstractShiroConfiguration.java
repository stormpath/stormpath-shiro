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
package com.stormpath.shiro.spring.config;

import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.event.EventBus;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.mgt.SessionsSecurityManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.session.mgt.DefaultSessionManager;
import org.apache.shiro.session.mgt.SessionManager;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @since 0.7.0
 */
public class AbstractShiroConfiguration {

    @Autowired(required = false)
    private CacheManager cacheManager;

    @Autowired
    private EventBus eventBus;

    protected SessionsSecurityManager securityManager(List<Realm> realms, SessionManager sessionManager) {
        SessionsSecurityManager securityManager = createSecurityManager();
        securityManager.setRealms(realms);
        securityManager.setSessionManager(sessionManager);
        securityManager.setEventBus(eventBus);

        if (cacheManager != null) {
            securityManager.setCacheManager(cacheManager);
        }

        return securityManager;
    }

    protected SessionManager sessionManager() {
        return new DefaultSessionManager();
    }

    protected SessionsSecurityManager createSecurityManager() {
        return new DefaultSecurityManager();
    }



}
