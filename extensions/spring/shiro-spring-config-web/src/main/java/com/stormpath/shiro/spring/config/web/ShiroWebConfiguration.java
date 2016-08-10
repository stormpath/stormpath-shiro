package com.stormpath.shiro.spring.config.web;

import com.stormpath.shiro.spring.config.ShiroBeanLifecycleConfiguration;
import org.apache.shiro.mgt.SessionsSecurityManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.session.mgt.SessionManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.List;

@Configuration
@Import({ShiroBeanLifecycleConfiguration.class})
public class ShiroWebConfiguration extends AbstractShiroWebConfiguration {

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
