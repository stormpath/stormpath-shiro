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
import com.stormpath.sdk.servlet.config.Config;
import com.stormpath.sdk.servlet.config.ConfigLoader;
import com.stormpath.sdk.servlet.event.RequestEventListener;
import com.stormpath.sdk.servlet.event.impl.EventPublisherFactory;
import com.stormpath.shiro.realm.PassthroughApplicationRealm;
import com.stormpath.shiro.servlet.config.MapLookup;
import com.stormpath.shiro.servlet.config.ShiroIniConfigLoader;
import com.stormpath.shiro.servlet.filter.StormpathShiroFilterChainResolverFactory;
import org.apache.shiro.config.CommonsInterpolator;
import org.apache.shiro.config.ConfigurationException;
import org.apache.shiro.config.Ini;
import org.apache.shiro.util.CollectionUtils;
import org.apache.shiro.util.Factory;
import org.apache.shiro.web.config.IniFilterChainResolverFactory;
import org.apache.shiro.web.config.WebIniSecurityManagerFactory;
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
 * The default objects can be over written, the defaults can be found in <code>com.stormpath.shiro.servlet.config.stormpath-shiro.ini</code>
 *
 * @since 0.7.0
 */
public class StormpathShiroIniEnvironment extends IniWebEnvironment {

    final private Logger log = LoggerFactory.getLogger(StormpathShiroIniEnvironment.class);

    private ConfigLoader configLoader;

    final private Map<String, Object> defaultEnvironmentObjects = new HashMap<>();

    final private static String DEFAULTS_STORMPATH_CLIENT_PROPERTY = "stormpathClient";

    final private Map<String, String> stormpathInterpolationMap = new HashMap<>();

    public StormpathShiroIniEnvironment() {

        WebIniSecurityManagerFactory factory = new WebIniSecurityManagerFactory();
        CommonsInterpolator interpolator = new CommonsInterpolator();
        // allow for ini files to contain ${stormpath.*} keys to be interpolated after config is loaded
        interpolator.getConfigurationInterpolator().addDefaultLookup(new MapLookup(stormpathInterpolationMap));

        factory.getReflectionBuilder().setInterpolator(interpolator);
        setSecurityManagerFactory(factory);
    }

    @Override
    protected Ini parseConfig() {
        return addDefaultsToIni(super.parseConfig());
    }

    @Override
    protected Ini getFrameworkIni() {
        return Ini.fromResourcePath("classpath:com/stormpath/shiro/servlet/config/stormpath-shiro.ini");
    }

    /**
     * Adds a default filter chain path <code>/** = authc</code> if one is not found in the Ini file, otherwise
     * returns the <code>ini</code> object untouched.
     *
     * @param ini The configuration Ini object to be updated
     * @return an updated configuration Ini.
     */
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
        Config stormpathConfig = configureStormpathEnvironment(); // pull values from the [stormpath] section
        stormpathInterpolationMap.putAll(stormpathConfig); // values from the shiro.ini files will be interpolated

        // Make the servlet context available to beans
        defaultEnvironmentObjects.put("servletContext", getServletContext());

        try {
            // Put the Stormpath Event Listener in the environment map, so the EventBus can be configured if needed.
            RequestEventListener requestEventListener = stormpathConfig.getInstance(EventPublisherFactory.REQUEST_EVENT_LISTENER);
            defaultEnvironmentObjects.put("stormpathRequestEventListener", requestEventListener);
        }
        catch (ServletException e) {
            throw new ConfigurationException("Could not get instance of Stormpath event listener. ", e);
        }

        this.objects.clear();

        // this causes the ReflectionBuilder to parse the config
        WebSecurityManager securityManager = createWebSecurityManager();
        setWebSecurityManager(securityManager);

        // After the ReflectionBuilder parse the config, we nee to pull out the Stormpath Client
        // and make it available in the ServletContext.  This is needed for other parts of the Stormpath API.
        Factory clientFactory = getObject(DEFAULTS_STORMPATH_CLIENT_PROPERTY, Factory.class);
        log.debug("Updating Client in ServletContext, with instance configured via shiro.ini");
        getServletContext().setAttribute(Client.class.getName(), clientFactory.getInstance());

        // and finally get the FilterChainResolver
        FilterChainResolver resolver = createFilterChainResolver();
        if (resolver != null) {
            setFilterChainResolver(resolver);
        }
    }

    private Config configureStormpathEnvironment() {
        return ensureConfigLoader().createConfig(getServletContext());
    }

    ConfigLoader ensureConfigLoader() {
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

    /**
     * Wraps the original FilterChainResolver in a priority based instance, which will detect Stormpath API based logins
     * (form, auth headers, etc).
     * @return
     */
    @Override
    protected FilterChainResolver createFilterChainResolver() {

        FilterChainResolver originalFilterChainResolver = super.createFilterChainResolver();

        if (originalFilterChainResolver == null) {
            return null;
        }

        return getFilterChainResolverFactory(originalFilterChainResolver).getInstance();
    }

    /*
     * Exposed to facilitate testing.
     */
    Factory<? extends FilterChainResolver> getFilterChainResolverFactory(FilterChainResolver originalFilterChainResolver) {
        return new StormpathShiroFilterChainResolverFactory(originalFilterChainResolver, getServletContext());
    }

    @Override
    protected Map<String, Object> getDefaults() {
        Map<String, Object> defaults = super.getDefaults();
        defaults.putAll(defaultEnvironmentObjects);
        return defaults;
    }
}
