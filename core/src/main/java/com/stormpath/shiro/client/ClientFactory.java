/*
 * Copyright 2013 Stormpath, Inc.
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
package com.stormpath.shiro.client;

import com.stormpath.sdk.client.Client;
import com.stormpath.sdk.client.ClientBuilder;
import com.stormpath.shiro.cache.ShiroCacheManager;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.util.AbstractFactory;

import java.io.InputStream;
import java.io.Reader;
import java.util.Properties;

/**
 * A simple bridge component that allows a Stormpath SDK Client to be created via Shiro's
 * {@link org.apache.shiro.util.Factory Factory} concept.
 * <p/>
 * As this class is a simple bridge between APIs, it does not do much - all configuration properties are immediately
 * passed through to an internal {@link com.stormpath.sdk.client.ClientBuilder ClientBuilder} instance, and the
 * {@link #createInstance()} implementation merely calls {@link com.stormpath.sdk.client.ClientBuilder#build()}.
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
 * @see ClientBuilder
 * @see ClientBuilder#setApiKeyFileLocation(String)
 * @since 0.1
 */
public class ClientFactory extends AbstractFactory<Client> {

    private ClientBuilder clientBuilder;

    public ClientFactory() {
        super();
        this.clientBuilder = new ClientBuilder();
    }

    @Override
    protected Client createInstance() {
        return clientBuilder.build();
    }

    public ClientBuilder getClientBuilder() {
        return clientBuilder;
    }

    public void setClientBuilder(ClientBuilder clientBuilder) {
        this.clientBuilder = clientBuilder;
    }

    /**
     * Calls {@code clientBuilder.}{@link ClientBuilder#setApiKeyFileLocation(String) setApiKeyFileLocation(location)}.
     * See that JavaDoc for expected syntax/format.
     *
     * @param apiKeyFileLocation the file, classpath or url location of the API Key {@code .properties} file to load when
     *                           constructing the API Key to use for communicating with the Stormpath REST API.
     * @see ClientBuilder#setApiKeyFileLocation(String)
     * @since 0.2
     */
    public void setApiKeyFileLocation(String apiKeyFileLocation) {
        this.clientBuilder.setApiKeyFileLocation(apiKeyFileLocation);
    }

    /**
     * Calls {@code clientBuilder.}{@link ClientBuilder#setApiKeyInputStream(java.io.InputStream) setApiKeyInputStream}.
     *
     * @param apiKeyInputStream the InputStream to use to construct a configuration Properties instance.
     * @see ClientBuilder#setApiKeyInputStream(java.io.InputStream)
     * @since 0.2
     */
    public void setApiKeyInputStream(InputStream apiKeyInputStream) {
        this.clientBuilder.setApiKeyInputStream(apiKeyInputStream);
    }

    /**
     * Calls {@code clientBuilder.}{@link ClientBuilder#setApiKeyReader(java.io.Reader) setApiKeyReader}.
     *
     * @param apiKeyReader the reader to use to construct a configuration Properties instance.
     * @see ClientBuilder#setApiKeyReader(java.io.Reader)
     * @since 0.2
     */
    public void setApiKeyReader(Reader apiKeyReader) {
        this.clientBuilder.setApiKeyReader(apiKeyReader);
    }

    /**
     * Calls {@code clientBuilder.}{@link ClientBuilder#setApiKeyProperties(java.util.Properties)}.
     *
     * @param properties the properties instance to use to load the API Key ID and Secret.
     * @see ClientBuilder#setApiKeyProperties(java.util.Properties)
     * @since 0.2
     */
    public void setApiKeyProperties(Properties properties) {
        this.clientBuilder.setApiKeyProperties(properties);
    }

    /**
     * Calls {@code clientBuilder.}{@link ClientBuilder#setApiKeyIdPropertyName(String) setApiKeyIdPropertyName}.
     *
     * @param apiKeyIdPropertyName the name used to query for the API Key ID from a Properties instance.
     * @see ClientBuilder#setApiKeyIdPropertyName(String)
     * @since 0.2
     */
    public void setApiKeyIdPropertyName(String apiKeyIdPropertyName) {
        this.clientBuilder.setApiKeyIdPropertyName(apiKeyIdPropertyName);
    }

    /**
     * Calls {@code clientBuilder.}{@link ClientBuilder#setApiKeySecretPropertyName(String) setApiKeySecretPropertyName}.
     *
     * @param apiKeySecretPropertyName the name used to query for the API Key Secret from a Properties instance.
     * @see ClientBuilder#setApiKeySecretPropertyName(String)
     * @since 0.2
     */
    public void setApiKeySecretPropertyName(String apiKeySecretPropertyName) {
        this.clientBuilder.setApiKeySecretPropertyName(apiKeySecretPropertyName);
    }

    /**
     * Uses the specified Shiro {@link CacheManager} instance as the Stormpath SDK Client's CacheManager, allowing both
     * Shiro and Stormpath SDK to share the same cache mechanism.
     * <p/>
     * If for some reason you don't want to share the same cache mechanism, you can explicitly set a Stormpath SDK-only
     * {@link com.stormpath.sdk.cache.CacheManager CacheManager} instance via the
     * {@link #setStormpathCacheManager(com.stormpath.sdk.cache.CacheManager) setStormpathCacheManager} method.
     *
     * @param cacheManager the Shiro CacheManager to use for the Stormpath SDK Client's caching needs.
     * @since 0.4.0
     * @see #setStormpathCacheManager(com.stormpath.sdk.cache.CacheManager)
     */
    public void setCacheManager(CacheManager cacheManager) {
        com.stormpath.sdk.cache.CacheManager stormpathCacheManager =
                new ShiroCacheManager(cacheManager);
        this.clientBuilder.setCacheManager(stormpathCacheManager);
    }

    /**
     * Calls {@code clientBuilder.}{@link ClientBuilder#setCacheManager(com.stormpath.sdk.cache.CacheManager) setCacheManager}
     * using the specified Stormpath {@link com.stormpath.sdk.cache.CacheManager CacheManager} instance, but <b>note:</b>
     * This method should only be used if the Stormpath SDK should use a <em>different</em> CacheManager than what
     * Shiro uses.
     * <p/>
     * If you prefer that Shiro and the Stormpath SDK use the same cache mechanism to reduce complexity/configuration,
     * configure your preferred Shiro {@code cacheManager} first and then use that cacheManager by calling the
     * {@link #setCacheManager(org.apache.shiro.cache.CacheManager) setCacheManager(shiroCacheManager)} method
     * instead of this one.
     *
     * @param cacheManager the Storpmath SDK-specific CacheManager to use for the Stormpath SDK Client's caching needs.
     * @since 0.4.0
     * @see #setCacheManager(org.apache.shiro.cache.CacheManager)
     */
    public void setStormpathCacheManager(com.stormpath.sdk.cache.CacheManager cacheManager) {
        this.clientBuilder.setCacheManager(cacheManager);
    }
}
