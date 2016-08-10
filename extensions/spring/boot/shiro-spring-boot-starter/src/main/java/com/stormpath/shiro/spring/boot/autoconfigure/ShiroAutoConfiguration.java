package com.stormpath.shiro.spring.boot.autoconfigure;

import com.stormpath.shiro.spring.config.AbstractShiroConfiguration;
import com.stormpath.shiro.spring.config.ShiroBeanLifecycleConfiguration;
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
@Import(ShiroBeanLifecycleConfiguration.class)
@SuppressWarnings("SpringFacetCodeInspection")
@ConditionalOnProperty(name = "shiro.enabled", matchIfMissing = true)
public class ShiroAutoConfiguration extends AbstractShiroConfiguration {

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
