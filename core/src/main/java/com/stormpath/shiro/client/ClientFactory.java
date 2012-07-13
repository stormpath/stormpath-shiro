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
package com.stormpath.shiro.client;

import com.stormpath.sdk.client.Client;
import com.stormpath.sdk.client.ClientBuilder;
import org.apache.shiro.util.AbstractFactory;

/**
 * A simple bridge component that allows a Stormpath SDK Client to be created via Shiro's
 * {@link org.apache.shiro.util.Factory Factory} concept.
 * <p/>
 * As this class is a simple bridge between APIs, it does not do much - all configuration must performed on the nested
 * {@link com.stormpath.sdk.client.ClientBuilder ClientBuilder} instance, and the
 * {@link #createInstance()} implementation merely calls {@link com.stormpath.sdk.client.ClientBuilder#build()}.
 * <h2>Usage</h2>
 * Example {@code shiro.ini} configuration:
 *
 * <pre>
 * [main]
 * ...
 * stormpathClient = com.stormpath.shiro.client.ClientFactory
 * stormpathClient.builder.apiKeyFileLocation = /home/myhomedir/.stormpath/apiKey.properties
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
}
