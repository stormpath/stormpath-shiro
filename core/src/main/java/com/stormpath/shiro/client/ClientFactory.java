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
 * <h2>Usage</h2>
 * Example {@code shiro.ini} configuration:
 * <p/>
 * <pre>
 * [main]
 * ...
 * stormpathClient = com.stormpath.shiro.client.ClientFactory
 * stormpathClient.apiKeyFileLocation = /home/myhomedir/.stormpath/apiKey.properties
 *
 * stormpathRealm = com.stormpath.shiro.realm.ApplicationRealm
 * stormpathRealm.client = $stormpathClient
 * stormpathRealm.applicationRestUrl = https://api.stormpath.com/v1/applications/someExampleIdHere
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
     *                 constructing the API Key to use for communicating with the Stormpath REST API.
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
     * @since 0.2
     * @see ClientBuilder#setApiKeyInputStream(java.io.InputStream)
     */
    public void setApiKeyInputStream(InputStream apiKeyInputStream) {
        this.clientBuilder.setApiKeyInputStream(apiKeyInputStream);
    }

    /**
     * Calls {@code clientBuilder.}{@link ClientBuilder#setApiKeyReader(java.io.Reader) setApiKeyReader}.
     *
     * @param apiKeyReader the reader to use to construct a configuration Properties instance.
     * @since 0.2
     * @see ClientBuilder#setApiKeyReader(java.io.Reader)
     */
    public void setApiKeyReader(Reader apiKeyReader) {
        this.clientBuilder.setApiKeyReader(apiKeyReader);
    }

    /**
     * Calls {@code clientBuilder.}{@link ClientBuilder#setApiKeyProperties(java.util.Properties)}.
     *
     * @param properties the properties instance to use to load the API Key ID and Secret.
     * @since 0.2
     * @see ClientBuilder#setApiKeyProperties(java.util.Properties)
     */
    public void setApiKeyProperties(Properties properties) {
        this.clientBuilder.setApiKeyProperties(properties);
    }

    /**
     * Calls {@code clientBuilder.}{@link ClientBuilder#setApiKeyIdPropertyName(String) setApiKeyIdPropertyName}.
     *
     * @param apiKeyIdPropertyName the name used to query for the API Key ID from a Properties instance.
     * @since 0.2
     * @see ClientBuilder#setApiKeyIdPropertyName(String)
     */
    public void setApiKeyIdPropertyName(String apiKeyIdPropertyName) {
        this.clientBuilder.setApiKeyIdPropertyName(apiKeyIdPropertyName);
    }

    /**
     * Calls {@code clientBuilder.}{@link ClientBuilder#setApiKeySecretPropertyName(String) setApiKeySecretPropertyName}.
     *
     * @param apiKeySecretPropertyName the name used to query for the API Key Secret from a Properties instance.
     * @since 0.2
     * @see ClientBuilder#setApiKeySecretPropertyName(String)
     */
    public void setApiKeySecretPropertyName(String apiKeySecretPropertyName) {
        this.clientBuilder.setApiKeySecretPropertyName(apiKeySecretPropertyName);
    }
}
