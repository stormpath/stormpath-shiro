package com.stormpath.shiro.spring.config.web;

import com.stormpath.shiro.spring.config.AbstractShiroConfiguration;
import org.apache.shiro.mgt.SessionsSecurityManager;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;

public abstract class AbstractShiroWebConfiguration extends AbstractShiroConfiguration {

    protected SessionsSecurityManager createSecurityManager() {

        return new DefaultWebSecurityManager();
    }

    protected SessionManager sessionManager() {
        DefaultWebSessionManager webSessionManager = new DefaultWebSessionManager();
        webSessionManager.setSessionIdCookieEnabled(false);
        webSessionManager.setSessionIdUrlRewritingEnabled(false);
        return webSessionManager;
    }
}
