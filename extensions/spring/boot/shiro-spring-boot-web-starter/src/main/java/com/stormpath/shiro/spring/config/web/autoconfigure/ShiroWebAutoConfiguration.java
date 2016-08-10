package com.stormpath.shiro.spring.config.web.autoconfigure;

import com.stormpath.shiro.spring.config.web.AbstractShiroWebConfiguration;
import org.apache.shiro.mgt.SessionsSecurityManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.session.mgt.SessionManager;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.List;

@Configuration
@Import(ShiroBeanLifecycleAutoConfiguration.class)
@ConditionalOnProperty(name = "shiro.web.enabled", matchIfMissing = true)
public class ShiroWebAutoConfiguration extends AbstractShiroWebConfiguration {

    @Bean
    @ConditionalOnMissingBean
    @Override
    protected SessionManager sessionManager() {
        return super.sessionManager();
    }

    @Bean
    @ConditionalOnMissingBean
    @Override
    protected SessionsSecurityManager securityManager(List<Realm> realms, SessionManager sessionManager) {
        return super.securityManager(realms, sessionManager);
    }

}
