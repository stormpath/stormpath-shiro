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

import org.apache.shiro.mgt.SessionsSecurityManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.session.mgt.SessionManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.List;

/**
 * @since 0.7.0
 */
@Configuration
@Import({ShiroBeanConfiguration.class})
public class ShiroConfiguration extends AbstractShiroConfiguration {

    @Bean
    @Override
    protected SessionsSecurityManager securityManager(List<Realm> realms, SessionManager sessionManager) {
        return super.securityManager(realms, sessionManager);
    }

    @Bean
    @Override
    protected SessionManager sessionManager() {
        return super.sessionManager();
    }
}
