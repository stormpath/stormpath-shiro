/*
 * Copyright 2015 Stormpath, Inc.
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
package com.stormpath.shiro.config;

import com.stormpath.sdk.api.ApiKey;
import com.stormpath.sdk.api.ApiKeyBuilder;
import com.stormpath.sdk.api.ApiKeys;
import com.stormpath.sdk.cache.CacheManager;
import com.stormpath.sdk.client.*;
import com.stormpath.sdk.lang.Classes;
import com.stormpath.sdk.lang.Strings;

import java.util.*;

import com.stormpath.sdk.impl.config.PropertiesSource;

/**
 *
 */
public class DefaultClientFactory {

    public static final String STORMPATH_API_KEY_FILE = "stormpath.client.apiKey.file";
    public static final String STORMPATH_AUTHENTICATION_SCHEME = "stormpath.client.authenticationScheme";

    public static final String STORMPATH_CACHE_MANAGER = "stormpath.client.cacheManager";

    public static final String STORMPATH_PROXY_HOST = "stormpath.client.proxy.host";
    public static final String STORMPATH_PROXY_PORT = "stormpath.client.proxy.port";
    public static final String STORMPATH_PROXY_USERNAME = "stormpath.client.proxy.username";
    public static final String STORMAPTH_PROXY_PASSWORD = "stormpath.client.proxy.password";

    public static final String STORMPATH_APPLICATION_HREF = "stormpath.application.href";

    public static final String STORMPATH_BASEURL = "stormpath.client.baseUrl";

    private Map<String, String> config;


    public Client createClient() {

        this.config = createConfig();

        ClientBuilder builder = Clients.builder();

        applyBaseUrl(builder);

        applyApiKey(builder);

        applyProxy(builder);

        applyAuthenticationScheme(builder);

        applyCacheManager(builder);

        return builder.build();
    }

    protected void applyBaseUrl(ClientBuilder builder) {

        String baseUrl = config.get(STORMPATH_BASEURL);
        if (Strings.hasText(baseUrl)) {
            builder.setBaseUrl(baseUrl);
        }
    }

    protected void applyCacheManager(ClientBuilder builder) {

        CacheManager cacheManager;

        String cacheManagerClass = config.get(STORMPATH_CACHE_MANAGER);
        if (Strings.hasText(cacheManagerClass)) {
            cacheManager = Classes.newInstance(cacheManagerClass);
            builder.setCacheManager(cacheManager);
        }
    }

    protected void applyApiKey(ClientBuilder clientBuilder) {
        ApiKey apiKey = createApiKey();
        clientBuilder.setApiKey(apiKey);
    }

    protected ApiKey createApiKey() {

        ApiKeyBuilder apiKeyBuilder = ApiKeys.builder();

        String value = config.get("stormpath.client.apiKey.id");
        if (Strings.hasText(value)) {
            apiKeyBuilder.setId(value);
        }

        //check for API Key ID embedded in the properties configuration
        value = config.get("stormpath.client.apiKey.secret");
        if (Strings.hasText(value)) {
            apiKeyBuilder.setSecret(value);
        }

        value = config.get(STORMPATH_API_KEY_FILE);
        if (Strings.hasText(value)) {
            apiKeyBuilder.setFileLocation(value);
        }

        return apiKeyBuilder.build();
    }

    protected void applyProxy(ClientBuilder builder) {

        String proxyHost = config.get(STORMPATH_PROXY_HOST);
        if (!Strings.hasText(proxyHost)) {
            return;
        }

        //otherwise, proxy config is present:

        Proxy proxy;

        int port = 80; //default
        String portValue = config.get(STORMPATH_PROXY_PORT);
        if (Strings.hasText(portValue)) {
            port = Integer.parseInt(portValue);
        }

        String proxyUsername = config.get(STORMPATH_PROXY_USERNAME);
        String proxyPassword = config.get(STORMAPTH_PROXY_PASSWORD);

        if (Strings.hasText(proxyUsername) || Strings.hasText(proxyPassword)) {
            proxy = new Proxy(proxyHost, port, proxyUsername, proxyPassword);
        } else {
            proxy = new Proxy(proxyHost, port);
        }

        builder.setProxy(proxy);
    }

    protected void applyAuthenticationScheme(ClientBuilder builder) {
        String schemeName = config.get(STORMPATH_AUTHENTICATION_SCHEME);
        if (Strings.hasText(schemeName)) {
            AuthenticationScheme scheme = AuthenticationScheme.valueOf(schemeName.toUpperCase(Locale.ENGLISH));
            builder.setAuthenticationScheme(scheme);
        }
    }

    protected Map<String, String> createConfig() {

        Map<String, String> configMap = new HashMap<String, String>();

        for (PropertiesSource source : getPropertySources()) {
            configMap.putAll(source.getProperties());
        }
        return configMap;
    }

    protected Collection<PropertiesSource> getPropertySources() {
        return Collections.emptyList();
    }
}
