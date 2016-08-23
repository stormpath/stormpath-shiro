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
package com.stormpath.shiro.servlet.config;

import com.stormpath.sdk.api.ApiKey;
import com.stormpath.sdk.api.ApiKeyBuilder;
import com.stormpath.sdk.api.ApiKeys;
import com.stormpath.sdk.client.Client;
import com.stormpath.sdk.client.ClientBuilder;
import com.stormpath.sdk.lang.Strings;
import com.stormpath.sdk.servlet.client.DefaultServletContextClientFactory;
import com.stormpath.sdk.servlet.config.Config;
import com.stormpath.shiro.cache.ShiroCacheManager;
import com.stormpath.shiro.config.ClientFactory;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.util.AbstractFactory;

import javax.servlet.ServletContext;

/**
 * A simple bridge component that allows a Stormpath SDK Client to be created via Shiro's
 * {@link org.apache.shiro.util.Factory Factory} concept.
 * <p/>
 * As this class is a simple bridge between APIs, it does not do much - all configuration properties are
 * passed through to an internal {@link DefaultServletContextClientFactory} instance, and the
 * {@link #createInstance()} implementation merely calls {@link DefaultServletContextClientFactory#createClient(ServletContext)}()}.
 * <h5>Usage</h5>
 * Example {@code shiro.ini} configuration:
 * <p/>
 * <pre>
 * [main]
 * ...
 * cacheManager = some.impl.of.org.apache.shiro.cache.CacheManager
 * securityManager.cacheManager = $cacheManager
 *
 * stormpathClient = com.stormpath.shiro.client.ClientFactory
 * stormpathClient.apiKeyFileLocation = /home/myhomedir/.stormpath/apiKey.properties
 * stormpathClient.cacheManager = $cacheManager
 *
 * stormpathRealm = com.stormpath.shiro.realm.ApplicationRealm
 * stormpathRealm.client = $stormpathClient
 * stormpathRealm.applicationRestUrl = https://api.stormpath.com/v1/applications/yourAppIdHere
 *
 * securityManager.realm = $stormpathRealm
 *
 * ...
 * </pre>
 *
 *
 * TODO: move most of these bits into stormpath-shiro-core
 *
 */
public class StormpathWebClientFactory extends AbstractFactory<Client> implements ClientFactory {

    private CacheManager cacheManager = null;
    private String apiKeyFileLocation = null;

    private String baseUrl = null;
    private String apiKeyId = null;
    private String apiKeySecret = null;



    final private ServletContext servletContext;

    public StormpathWebClientFactory(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    @Override
    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    @Override
    public void setApiKeyFileLocation(String apiKeyFileLocation) {
        this.apiKeyFileLocation = apiKeyFileLocation;
    }

    @Override
    public void setApiKeyId(String apiKeyId) {
        this.apiKeyId = apiKeyId;
    }

    @Override
    public void setApiKeySecret(String apiKeySecret) {
        this.apiKeySecret = apiKeySecret;
    }

    @Override
    public void setCacheManager(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    @Override
    protected Client createInstance() {
        return new ShiroBridgeServletContextClientFactory().createClient(servletContext);
    }


    /**
     * Wrapper around DefaultServletContextClientFactory, that allows overriding of the <code>baseUrl</code>, api key
     * (<code>id</code>, <code>secret</code>, <code>fileLocation</code>) and <code>cacheManager</code>.
     */
    private class ShiroBridgeServletContextClientFactory extends DefaultServletContextClientFactory {


        @Override
        protected void applyBaseUrl(ClientBuilder builder) {
            if (Strings.hasText(baseUrl)) {
                builder.setBaseUrl(baseUrl);
            }
            else {
                super.applyBaseUrl(builder);
            }
        }

        @Override
        @SuppressWarnings("PMD.NPathComplexity")
        protected ApiKey createApiKey() {

            ApiKeyBuilder apiKeyBuilder = ApiKeys.builder();
            Config config = getConfig();

            String value = Strings.hasText(apiKeyId) ? apiKeyId : config.get("stormpath.client.apiKey.id");
            if (Strings.hasText(value)) {
                apiKeyBuilder.setId(value);
            }

            //check for API Key ID embedded in the properties configuration
            value = Strings.hasText(apiKeySecret) ? apiKeySecret : config.get("stormpath.client.apiKey.secret");
            if (Strings.hasText(value)) {
                apiKeyBuilder.setSecret(value);
            }

            value = Strings.hasText(apiKeyFileLocation) ? apiKeyFileLocation : config.get(STORMPATH_API_KEY_FILE);
            if (Strings.hasText(value)) {
                apiKeyBuilder.setFileLocation(value);
            }

            return apiKeyBuilder.build();
        }

        @Override
        protected void applyCacheManager(ClientBuilder builder) {

            if (cacheManager == null) {
                super.applyCacheManager(builder);
            }
            else {
                com.stormpath.sdk.cache.CacheManager stormpathCacheManager = new ShiroCacheManager(cacheManager);
                builder.setCacheManager(stormpathCacheManager);
            }
        }
    }
}
