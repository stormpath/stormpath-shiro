package com.stormpath.shiro.spring.boot.autoconfigure;

import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.client.Client;
import com.stormpath.shiro.realm.ApplicationRealm;
import org.apache.shiro.realm.Realm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@SuppressWarnings("SpringFacetCodeInspection")
@Configuration
@ConditionalOnProperty(name = "stormpath.shiro.enabled", matchIfMissing = true)
@AutoConfigureOrder(0)
public class StormpathShiroAutoConfiguration {

    @Autowired
    private Client client;

    @Autowired
    private Application application;

    @Bean(name = "stormpathRealm")
    @ConditionalOnMissingBean
    public Realm getRealm() {
        ApplicationRealm realm = new ApplicationRealm();
        realm.setApplicationRestUrl(application.getHref());
        realm.setClient(client);
        return realm;
    }

}
