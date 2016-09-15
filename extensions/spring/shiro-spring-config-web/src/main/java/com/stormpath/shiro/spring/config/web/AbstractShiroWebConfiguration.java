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
package com.stormpath.shiro.spring.config.web;

import com.stormpath.shiro.spring.config.AbstractShiroConfiguration;
import org.apache.shiro.mgt.SessionsSecurityManager;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.springframework.beans.factory.annotation.Value;

/**
 * @since 0.7.0
 */
public class AbstractShiroWebConfiguration extends AbstractShiroConfiguration {

    @Value("#{ @environment['shiro.sessionManager.sessionIdCookieEnabled'] ?: true }")
    protected boolean sessionIdCookieEnabled;

    @Value("#{ @environment['shiro.sessionManager.sessionIdUrlRewritingEnabled'] ?: true }")
    protected boolean sessionIdUrlRewritingEnabled;

    protected SessionsSecurityManager createSecurityManager() {

        return new DefaultWebSecurityManager();
    }

    protected SessionManager sessionManager() {
        DefaultWebSessionManager webSessionManager = new DefaultWebSessionManager();
        webSessionManager.setSessionIdCookieEnabled(sessionIdCookieEnabled);
        webSessionManager.setSessionIdUrlRewritingEnabled(sessionIdUrlRewritingEnabled);
        return webSessionManager;
    }
}
