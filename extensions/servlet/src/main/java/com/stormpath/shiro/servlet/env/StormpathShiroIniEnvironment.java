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
import com.stormpath.sdk.impl.io.ClasspathResource;
import com.stormpath.sdk.lang.Strings;
import com.stormpath.sdk.servlet.config.Config;
import com.stormpath.sdk.servlet.config.ConfigLoader;
import com.stormpath.sdk.servlet.config.impl.DefaultConfigFactory;
import com.stormpath.sdk.servlet.event.RequestEventListener;
import com.stormpath.sdk.servlet.event.impl.EventPublisherFactory;
import com.stormpath.shiro.realm.ApplicationRealm;
import com.stormpath.shiro.realm.PassthroughApplicationRealm;
import com.stormpath.shiro.servlet.config.ShiroIniConfigLoader;
import com.stormpath.shiro.servlet.config.StormpathWebClientFactory;
import com.stormpath.shiro.servlet.event.LogoutEventListener;
import com.stormpath.shiro.servlet.filter.StormpathShiroFilterChainResolverFactory;
import org.apache.shiro.config.ConfigurationException;
import org.apache.shiro.config.Ini;
import org.apache.shiro.config.IniSecurityManagerFactory;
import org.apache.shiro.util.CollectionUtils;
import org.apache.shiro.util.Factory;
import org.apache.shiro.web.config.IniFilterChainResolverFactory;
import org.apache.shiro.web.env.IniWebEnvironment;
import org.apache.shiro.web.filter.mgt.DefaultFilter;
import org.apache.shiro.web.filter.mgt.FilterChainResolver;
import org.apache.shiro.web.mgt.WebSecurityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import java.util.HashMap;
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
 * @since 0.7.0
 */
public class StormpathShiroIniEnvironment extends IniWebEnvironment {

    final private Logger log = LoggerFactory.getLogger(StormpathShiroIniEnvironment.class);

    private ConfigLoader configLoader;

    final private Map<String, Object> defaultEnvironmentObjects = new HashMap<>();

    final private static String STORMPATH_APPLICATION_HREF_PROPERTY = "stormpath.application.href";
    final private static String DEFAULTS_STORMPATH_CLIENT_PROPERTY = "stormpathClient";
    final private static String DEFAULTS_STORMPATH_REALM_PROPERTY = "stormpathRealm";
    final private static String NL = "\n";
    final private static String  SHIRO_STORMPATH_PROPERTIES_SOURCES =
                    ClasspathResource.SCHEME_PREFIX + "com/stormpath/sdk/servlet/config/web." + DefaultConfigFactory.STORMPATH_PROPERTIES + NL +
                    ClasspathResource.SCHEME_PREFIX + "com/stormpath/shiro/servlet/config/shiro." + DefaultConfigFactory.STORMPATH_PROPERTIES + NL +
                    ClasspathResource.SCHEME_PREFIX + DefaultConfigFactory.STORMPATH_PROPERTIES + NL +
                    "/WEB-INF/stormpath.properties" + NL +
                    DefaultConfigFactory.CONTEXT_PARAM_TOKEN + NL +
                    DefaultConfigFactory.ENVVARS_TOKEN + NL +
                    DefaultConfigFactory.SYSPROPS_TOKEN;



    @Override
    protected Ini parseConfig() {
        return addDefaultsToIni(super.parseConfig());
    }

    @Override
    protected Ini getFrameworkIni() {
        Ini ini = new Ini();

        // lazy associate the client with the realm, so changes can be made if needed.
        ini.setSectionProperty(IniSecurityManagerFactory.MAIN_SECTION_NAME, DEFAULTS_STORMPATH_REALM_PROPERTY+".client", "$"+DEFAULTS_STORMPATH_CLIENT_PROPERTY);

        // global properties 'shiro.*' are not loaded from the defaults, we must set it in the ini.
        ini.setSectionProperty(IniSecurityManagerFactory.MAIN_SECTION_NAME, "shiro.loginUrl", "/login");

        return ini;
    }

    private Ini addDefaultsToIni(Ini ini) {

        // protect the world if the URL section is missing
        Ini.Section urls = ini.getSection(IniFilterChainResolverFactory.URLS);
        Ini.Section filters = ini.getSection(IniFilterChainResolverFactory.FILTERS); // deprecated behavior
        if (CollectionUtils.isEmpty(urls) && CollectionUtils.isEmpty(filters)) {
            ini.setSectionProperty(IniFilterChainResolverFactory.URLS, "/**", DefaultFilter.authc.name());
        }
        return ini;
    }

    @Override
    protected void configure() {

        // create the config object
        Config stormpathConfig = configureStormpathEnvironment();

        // Chicken or egg problem. At this point we do NOT have a Stormpath Client, so we cannot use the
        // ApplicationResolver because that will force the client to be created, and would load before the
        // ReflectionBuilder had a chance to customize the client.
        // To keep things simple for now: if the app href is set, we just pass it on to the realm.
        ApplicationRealm realm = new PassthroughApplicationRealm();
        if (stormpathConfig.containsKey(STORMPATH_APPLICATION_HREF_PROPERTY)) {
            String appHref = stormpathConfig.get(STORMPATH_APPLICATION_HREF_PROPERTY);
            realm.setApplicationRestUrl(appHref);
        }
        defaultEnvironmentObjects.put(DEFAULTS_STORMPATH_CLIENT_PROPERTY, new StormpathWebClientFactory(getServletContext()));
        defaultEnvironmentObjects.put(DEFAULTS_STORMPATH_REALM_PROPERTY, realm);
        try {
            RequestEventListener requestEventListener = stormpathConfig.getInstance(EventPublisherFactory.REQUEST_EVENT_LISTENER);
            defaultEnvironmentObjects.put("stormpathRequestEventListener", requestEventListener);
            defaultEnvironmentObjects.put("stormpathLogoutListener", new LogoutEventListener());
        }
        catch (ServletException e) {
            throw new ConfigurationException("Could not get instance of Stormpath event listener. ", e);
        }

        this.objects.clear();

        WebSecurityManager securityManager = createWebSecurityManager();
        setWebSecurityManager(securityManager);

        Factory clientFactory = getObject(DEFAULTS_STORMPATH_CLIENT_PROPERTY, Factory.class);
        log.debug("Updating Client in ServletContext, with instance configured via shiro.ini");
        getServletContext().setAttribute(Client.class.getName(), clientFactory.getInstance());

        FilterChainResolver resolver = createFilterChainResolver();
        if (resolver != null) {
            setFilterChainResolver(resolver);
        }

    }

    protected Config configureStormpathEnvironment() {

        String sourceDefs = getServletContext().getInitParameter(DefaultConfigFactory.STORMPATH_PROPERTIES_SOURCES);
        if (!Strings.hasText(sourceDefs)) {
            getServletContext().setInitParameter(DefaultConfigFactory.STORMPATH_PROPERTIES_SOURCES, SHIRO_STORMPATH_PROPERTIES_SOURCES);
        }

        return ensureConfigLoader().createConfig(getServletContext());
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

    @Override
    protected Map<String, Object> getDefaults() {
        Map<String, Object> defaults = super.getDefaults();
        defaults.putAll(defaultEnvironmentObjects);
        return defaults;
    }
}
