package com.stormpath.shiro.servlet.config

import org.apache.shiro.cache.MemoryConstrainedCacheManager
import org.testng.annotations.Test

import static org.testng.Assert.assertEquals
import static org.testng.Assert.assertSame

/**
 * Tests for {@link StormpathWebClientFactory}.
 */
class StormpathWebClientFactoryTest extends StormpathConfigTestSupport {

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
