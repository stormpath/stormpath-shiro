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
package com.stormpath.shiro.servlet.env;

import com.stormpath.sdk.client.Client;
import com.stormpath.sdk.servlet.config.ConfigLoader;
import com.stormpath.shiro.servlet.config.ClientFactory;
import com.stormpath.shiro.realm.PassthroughApplicationRealm;
import com.stormpath.shiro.servlet.config.ShiroIniConfigLoader;
import com.stormpath.shiro.servlet.config.StormpathWebClientFactory;
import com.stormpath.shiro.servlet.filter.StormpathShiroFilterChainResolverFactory;
import org.apache.shiro.config.Ini;
import org.apache.shiro.config.IniSecurityManagerFactory;
import org.apache.shiro.util.CollectionUtils;
import org.apache.shiro.util.Factory;
import org.apache.shiro.web.config.WebIniSecurityManagerFactory;
import org.apache.shiro.web.env.IniWebEnvironment;
import org.apache.shiro.web.filter.mgt.FilterChainResolver;
import org.apache.shiro.web.mgt.WebSecurityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * {@link IniWebEnvironment} implementation that creates a default Stormpath {@link PassthroughApplicationRealm}.
 *<BR/><BR/>
 * The default objects can be over written, the defaults for these are equivalent to:
 *
 * <code><pre>
 * [main]
 * stormpathClient = com.stormpath.shiro.web.servlet.config.StormpathWebClientFactory
 * stormpathRealm = com.stormpath.shiro.realm.PassthroughApplicationRealm
 * stormpathRealm.client = $stormpathClient
 * </pre></code>
 * <BR/>
 * Further <code>stormpath.*</code> properties are used to provide defaults.<BR/>
 * <code><pre>
 * stormpathClient.baseUrl = stormpath.client.baseUrl
 * stormpathClient.apiKeyFileLocation = stormpath.client.apiKey.file
 * stormpathClient.apiKeyId = stormpath.client.apiKey.id
 * stormpathClient.apiKeySecret = stormpath.client.apiKey.secret
 *
 * stormpathRealm.applicationRestUrl = stormpath.application.href
 * </pre></code>
 */
public class StormpathShiroIniEnvironment extends IniWebEnvironment {

    final private Logger log = LoggerFactory.getLogger(StormpathShiroIniEnvironment.class);

    private ConfigLoader configLoader;

    @Override
    @SuppressWarnings("PMD.AvoidReassigningParameters")
    public void setIni(Ini ini) {

        if (ini == null) {
            ini = new Ini();
        }

        addDefaultsToIni(ini);

        super.setIni(ini);
    }

    private Ini.Section getConfigSection(Ini ini) {

        Ini.Section configSection = ini.getSection(IniSecurityManagerFactory.MAIN_SECTION_NAME);
        if (CollectionUtils.isEmpty(configSection)) {
            configSection = ini.getSection(Ini.DEFAULT_SECTION_NAME);
            if (configSection == null) {
                configSection = ini.addSection(Ini.DEFAULT_SECTION_NAME);
            }
        }

        return configSection;
    }

    private void addDefaultsToIni(Ini ini) {

        // TODO: this is not ideal, we need to make shiro a bit more flexible
        // and this is tightly coupled with the following method
        Ini.Section configSection = getConfigSection(ini);

        // lazy associate the client with the realm, so changes can be made if needed.
        if (!configSection.containsKey("stormpathRealm.client")) {
            configSection.put("stormpathRealm.client", "$stormpathClient");
        }

        // global properties 'shiro.*' are not loaded from the defaults, we must set it in the ini.
        if (!configSection.containsKey("shiro.loginUrl")) {
            configSection.put("shiro.loginUrl", "/login");
        }
    }

    private Map<String, ?> getDefaultEnvironmentObjects() {

        Map<String, Object> defaults = new LinkedHashMap<String, Object>();
        defaults.put("stormpathClient", new StormpathWebClientFactory(getServletContext()));
        defaults.put("stormpathRealm", new PassthroughApplicationRealm());

        return defaults;
    }

    @Override
    protected void configure() {

        configureStormpathEnvironment();

        this.objects.clear();

        WebSecurityManager securityManager = createWebSecurityManager();
        setWebSecurityManager(securityManager);

        ClientFactory clientFactory = getObject("stormpathClient", ClientFactory.class);
        log.debug("Updating Client in ServletContext, with instance configured via shiro.ini");
        getServletContext().setAttribute(Client.class.getName(), clientFactory.getInstance());

        FilterChainResolver resolver = createFilterChainResolver();
        if (resolver != null) {
            setFilterChainResolver(resolver);
        }

    }

    protected void configureStormpathEnvironment() {
        ensureConfigLoader().createConfig(getServletContext());
    }

    protected ConfigLoader ensureConfigLoader() {
        if (configLoader == null) {
            configLoader = new ShiroIniConfigLoader(getIni());
        }
        return configLoader;
    }


    @Override
    public void destroy() throws Exception {
        ensureConfigLoader().destroyConfig(getServletContext());
        getServletContext().removeAttribute(Client.class.getName());
        super.destroy();
    }

    @Override
    protected FilterChainResolver createFilterChainResolver() {

        FilterChainResolver originalFilterChainResolver = super.createFilterChainResolver();

        if (originalFilterChainResolver == null) {
            return null;
        }

        return getFilterChainResolverFactory(originalFilterChainResolver).getInstance();
    }

    protected Factory<? extends FilterChainResolver> getFilterChainResolverFactory(FilterChainResolver originalFilterChainResolver) {
        return new StormpathShiroFilterChainResolverFactory(originalFilterChainResolver, getServletContext());
    }

// TODO think about putting these in Shiro proper

    @Override
    protected WebSecurityManager createWebSecurityManager() {
        WebIniSecurityManagerFactory factory = getSecurityManagerFactory();
        factory.setIni(getIni());

        WebSecurityManager wsm = (WebSecurityManager)factory.getInstance();

        //SHIRO-306 - get beans after they've been created (the call was before the factory.getInstance() call,
        //which always returned null.
        Map<String, ?> beans = factory.getBeans();
        if (!CollectionUtils.isEmpty(beans)) {
            this.objects.putAll(beans);
        }

        return wsm;
    }

    protected WebIniSecurityManagerFactory getSecurityManagerFactory() {
        // default implementation
//        WebIniSecurityManagerFactory factory = new WebIniSecurityManagerFactory();

        return new WebIniSecurityManagerFactoryWithDefaults(getDefaultEnvironmentObjects());
    }

    private static class WebIniSecurityManagerFactoryWithDefaults extends WebIniSecurityManagerFactory {

        final private Map<String, ?> environmentDefaults;

        public WebIniSecurityManagerFactoryWithDefaults(Map<String, ?> environmentDefaults) {
            super();
            this.environmentDefaults = environmentDefaults;
        }

        @Override
        protected Map<String, ?> createDefaults(Ini ini, Ini.Section mainSection) {
            Map<String, Object> defaults = new HashMap<String, Object>(super.createDefaults(ini, mainSection));
            defaults.putAll(environmentDefaults);
            return defaults;
        }
    }

}
