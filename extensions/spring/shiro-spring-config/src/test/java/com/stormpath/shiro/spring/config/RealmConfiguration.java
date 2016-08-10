package com.stormpath.shiro.spring.config;

import org.apache.shiro.realm.Realm;
import org.apache.shiro.realm.text.TextConfigurationRealm;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

@Configuration
public class RealmConfiguration {

    @Bean
    @DependsOn("lifecycleBeanPostProcessor")
    @SuppressWarnings("Duplicates")
    Realm getTextConfigurationRealm() {

        TextConfigurationRealm realm = new TextConfigurationRealm();
        realm.setUserDefinitions("joe.coder=password,user\n" +
                "jill.coder=password,admin");

        realm.setRoleDefinitions("admin=read,write\n" +
                "user=read");
        realm.setCachingEnabled(true);
        return realm;
    }
}
