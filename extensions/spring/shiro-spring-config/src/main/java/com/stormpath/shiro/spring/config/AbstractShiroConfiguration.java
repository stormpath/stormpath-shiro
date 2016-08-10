package com.stormpath.shiro.spring.config;

import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.mgt.SessionsSecurityManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.session.mgt.DefaultSessionManager;
import org.apache.shiro.session.mgt.SessionManager;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class AbstractShiroConfiguration {

    @Autowired(required = false)
    private CacheManager cacheManager;

    protected SessionsSecurityManager securityManager(List<Realm> realms, SessionManager sessionManager) {
        SessionsSecurityManager securityManager = createSecurityManager();
        securityManager.setRealms(realms);
        securityManager.setSessionManager(sessionManager);

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
