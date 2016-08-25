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
package com.stormpath.shiro.servlet.config

import com.stormpath.sdk.client.Client
import com.stormpath.sdk.servlet.config.Config
import com.stormpath.sdk.servlet.config.impl.DefaultConfigFactory
import com.stormpath.shiro.servlet.ShiroTestSupport
import org.apache.shiro.cache.MemoryConstrainedCacheManager
import org.easymock.IAnswer
import org.testng.annotations.Test

import javax.servlet.ServletContext

import static org.easymock.EasyMock.anyObject
import static org.easymock.EasyMock.expect
import static org.easymock.EasyMock.mock
import static org.easymock.EasyMock.replay
import static org.testng.Assert.assertEquals
import static org.testng.Assert.assertSame

/**
 * Tests for {@link StormpathWebClientFactory}.
 * @since 0.7.0
 */
class StormpathWebClientFactoryTest {

    Config config
    ServletContext servletContext

    protected void setupMocks() {

        final def delayedInitMap = new HashMap<String, Object>()
        final def configKey = "config"

        servletContext = mock(ServletContext)
        expect(servletContext.getInitParameter(DefaultConfigFactory.STORMPATH_PROPERTIES_SOURCES)).andReturn(null)
        expect(servletContext.getInitParameter(DefaultConfigFactory.STORMPATH_PROPERTIES)).andReturn(null)
        expect(servletContext.getResourceAsStream(anyObject())).andReturn(null).anyTimes()


        expect(servletContext.getAttribute(Config.getName())).andAnswer(new IAnswer<Object>() {
            @Override
            Object answer() throws Throwable {
                return delayedInitMap.get(configKey)
            }
        }).anyTimes()

        def client = mock(Client)
        expect(servletContext.getAttribute(Client.getName())).andReturn(client)

        replay servletContext, client

        config = new DefaultConfigFactory().createConfig(servletContext)
        delayedInitMap.put(configKey, config)
    }

    @Test
    public void testSettingShiroCacheManager() {

        setupMocks()

        def clientFactory = new StormpathWebClientFactory(servletContext)
        def cacheManager = new MemoryConstrainedCacheManager()
        clientFactory.setCacheManager(cacheManager)

        def client = clientFactory.getInstance()
        assertSame cacheManager, client.getCacheManager().SHIRO_CACHE_MANAGER
    }

    @Test
    public void testSettingBaseUrl() {

        def baseUrl = "http://baseUrl"
        setupMocks()

        def clientFactory = new StormpathWebClientFactory(servletContext)
        clientFactory.setBaseUrl(baseUrl)

        def client = clientFactory.getInstance()
        assertEquals baseUrl, client.dataStore.baseUrl
    }

    @Test
    public void testSettingClientApiKeyId() {

        def apiKeyId = "apiKeyId123"
        setupMocks()

        def clientFactory = new StormpathWebClientFactory(servletContext)
        clientFactory.setApiKeyId(apiKeyId)

        def client = clientFactory.getInstance()
        assertEquals apiKeyId, client.dataStore.apiKey.id
    }

    @Test
    public void testSettingClientApiKeySecret() {

        def apiKeySecret = "apiKeySecret123"
        setupMocks()

        def clientFactory = new StormpathWebClientFactory(servletContext)
        clientFactory.setApiKeySecret(apiKeySecret)

        def client = clientFactory.getInstance()
        assertEquals apiKeySecret, client.dataStore.apiKey.secret
    }

    @Test
    public void testSettingClientApiKeyLocation() {

        def apiKeyLocation = new File("src/test/resources/com/stormpath/shiro/config/fakeApiKey.properties").absolutePath
        setupMocks()

        def clientFactory = new StormpathWebClientFactory(servletContext)
        clientFactory.setApiKeyFileLocation(apiKeyLocation)

        def client = clientFactory.getInstance()
        assertEquals "fakeId", client.dataStore.apiKey.id
        assertEquals "fakeSecret", client.dataStore.apiKey.secret
    }

}
