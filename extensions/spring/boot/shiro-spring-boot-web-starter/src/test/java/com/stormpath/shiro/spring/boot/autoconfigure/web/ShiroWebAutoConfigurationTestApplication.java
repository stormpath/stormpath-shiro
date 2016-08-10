package com.stormpath.shiro.spring.boot.autoconfigure.web;


import com.stormpath.shiro.spring.config.web.DefaultShiroFilterChainDefinitionProvider;
import com.stormpath.shiro.spring.config.web.ShiroFilterChainDefinitionProvider;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.realm.text.TextConfigurationRealm;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableAutoConfiguration
public class ShiroWebAutoConfigurationTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShiroWebAutoConfigurationTestApplication.class, args);
    }

    @Bean
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

    @Bean
    ShiroFilterChainDefinitionProvider getFilterChainDefinitionProvider() {
        return new DefaultShiroFilterChainDefinitionProvider();
    }
}
