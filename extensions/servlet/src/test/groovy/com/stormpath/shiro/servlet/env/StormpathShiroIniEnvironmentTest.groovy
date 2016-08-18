package com.stormpath.shiro.servlet.env

import com.stormpath.sdk.client.Client
import com.stormpath.sdk.servlet.config.Config
import com.stormpath.sdk.servlet.config.ConfigLoader
import com.stormpath.sdk.servlet.config.impl.DefaultConfigFactory
import com.stormpath.sdk.servlet.filter.StormpathFilter
import com.stormpath.shiro.config.ClientFactory
import com.stormpath.shiro.config.DefaultClientFactory
import com.stormpath.shiro.realm.ApplicationRealm
import com.stormpath.shiro.servlet.ShiroTestSupportWithSystemProperties
import org.apache.shiro.config.Ini
import org.apache.shiro.util.Factory
import org.apache.shiro.web.config.IniFilterChainResolverFactory
import org.apache.shiro.web.filter.mgt.FilterChainResolver
import org.apache.shiro.web.mgt.DefaultWebSecurityManager
import org.easymock.Capture
import org.easymock.IAnswer
import org.easymock.IExpectationSetters
import org.hamcrest.Matchers
import org.testng.annotations.Test

import javax.servlet.ServletContext

import static org.easymock.EasyMock.*
import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.*
import static org.testng.Assert.assertNotNull
import static org.testng.Assert.assertSame

/**
 * Tests for {@link StormpathShiroIniEnvironment}.
 */
@Test(singleThreaded = true)
class StormpathShiroIniEnvironmentTest extends ShiroTestSupportWithSystemProperties {

    @Test
    public void testDefaultCreate() {

        def appHref = "http://testDefaultObjects"
        setSystemProperty(DefaultClientFactory.STORMPATH_APPLICATION_HREF, appHref)

        def servletContext = mock(ServletContext)

        def clientCapture = new Capture<Client>();

        final def delayedInitMap = new HashMap<String, Object>()
        final def configKey = "config"

        expectConfigFromServletContext(servletContext, delayedInitMap, configKey).anyTimes()

        expect(servletContext.getInitParameter(DefaultConfigFactory.STORMPATH_PROPERTIES_SOURCES)).andReturn(null)
        expect(servletContext.getInitParameter(DefaultConfigFactory.STORMPATH_PROPERTIES)).andReturn(null)
        expect(servletContext.getResourceAsStream(anyObject())).andReturn(null).anyTimes()
        servletContext.setAttribute(eq(Client.getName()), capture(clientCapture))

        replay servletContext

        def config = new DefaultConfigFactory().createConfig(servletContext)
        delayedInitMap.put(configKey, config)


        def ini = new Ini()
        doTestWithIni(ini, appHref, servletContext)
    }

    @Test
    public void testDefaultCreateWithNoIni() {

        def appHref = "http://testDefaultObjectsNoIni"
        setSystemProperty(DefaultClientFactory.STORMPATH_APPLICATION_HREF, appHref)

        def servletContext = mock(ServletContext)

        def clientCapture = new Capture<Client>();

        final def delayedInitMap = new HashMap<String, Object>()
        final def configKey = "config"

        expectConfigFromServletContext(servletContext, delayedInitMap, configKey).anyTimes()

        expect(servletContext.getInitParameter(DefaultConfigFactory.STORMPATH_PROPERTIES_SOURCES)).andReturn(null)
        expect(servletContext.getInitParameter(DefaultConfigFactory.STORMPATH_PROPERTIES)).andReturn(null)
        expect(servletContext.getResourceAsStream(anyObject())).andReturn(null).anyTimes()
        servletContext.setAttribute(eq(Client.getName()), capture(clientCapture))

        replay servletContext

        def config = new DefaultConfigFactory().createConfig(servletContext)
        delayedInitMap.put(configKey, config)


        doTestWithIni(null, appHref, servletContext)
    }

    @Test
    public void testCreateWithIniAppHref() {

        def appHref = "http://testCreateSecurityManagerIniAppHref"

        def servletContext = mock(ServletContext)

        def clientCapture = new Capture<Client>();

        final def delayedInitMap = new HashMap<String, Object>()
        final def configKey = "config"

        expectConfigFromServletContext(servletContext, delayedInitMap, configKey).anyTimes()

        expect(servletContext.getInitParameter(DefaultConfigFactory.STORMPATH_PROPERTIES_SOURCES)).andReturn(null)
        expect(servletContext.getInitParameter(DefaultConfigFactory.STORMPATH_PROPERTIES)).andReturn(null)
        expect(servletContext.getResourceAsStream(anyObject())).andReturn(null).anyTimes()
        servletContext.setAttribute(eq(Client.getName()), capture(clientCapture))

        replay servletContext

        def config = new DefaultConfigFactory().createConfig(servletContext)
        delayedInitMap.put(configKey, config)

        def ini = new Ini()
        ini.setSectionProperty("main", "stormpathRealm.applicationRestUrl", appHref)

        doTestWithIni(ini, appHref, servletContext)
    }

    @Test
    public void testCreateWithIniStormpathClientBaseUrl() {

        def appHref = "http://testCreateSecurityManagerIniAppHref"
        setSystemProperty(DefaultClientFactory.STORMPATH_APPLICATION_HREF, appHref)

        def baseUrl = "http://baseUrl"

        def ini = new Ini()
        ini.setSectionProperty("main", "stormpathClient.baseUrl", baseUrl)


        final def delayedInitMap = new HashMap<String, Object>()
        final def configKey = "config"

        def servletContext = mock(ServletContext)
        expect(servletContext.getInitParameter(DefaultConfigFactory.STORMPATH_PROPERTIES_SOURCES)).andReturn(null)
        expect(servletContext.getInitParameter(DefaultConfigFactory.STORMPATH_PROPERTIES)).andReturn(null)
        expect(servletContext.getResourceAsStream(anyObject())).andReturn(null).anyTimes()

        expectConfigFromServletContext(servletContext, delayedInitMap, configKey).anyTimes()

        def clientCapture = new Capture<Client>()
        def client = mock(Client)
        servletContext.setAttribute(eq(Client.getName()), capture(clientCapture))
        expectLastCall()

        replay servletContext, client

        def config = new DefaultConfigFactory().createConfig(servletContext)
        delayedInitMap.put(configKey, config)

        doTestWithIni(ini, appHref, servletContext)
        def actualClient = clientCapture.value

        assertSame baseUrl, actualClient.dataStore.baseUrl
    }

    @Test
    public void testSimpleFilterConfig() {

        def appHref = "http://testSimpleFilterConfig"

        def servletContext = mock(ServletContext)

        def clientCapture = new Capture<Client>();

        final def delayedInitMap = new HashMap<String, Object>()
        final def configKey = "config"

        expectConfigFromServletContext(servletContext, delayedInitMap, configKey).anyTimes()

        expect(servletContext.getInitParameter(DefaultConfigFactory.STORMPATH_PROPERTIES_SOURCES)).andReturn(null)
        expect(servletContext.getInitParameter(DefaultConfigFactory.STORMPATH_PROPERTIES)).andReturn(null)
        expect(servletContext.getResourceAsStream(anyObject())).andReturn(null).anyTimes()
        servletContext.setAttribute(eq(Client.getName()), capture(clientCapture))

        replay servletContext

        def config = new DefaultConfigFactory().createConfig(servletContext)
        delayedInitMap.put(configKey, config)

        def ini = new Ini()
        ini.setSectionProperty("main", "stormpathRealm.applicationRestUrl", appHref)
        // we need to have at least one path defined for the filterChain to be configured.
        ini.setSectionProperty(IniFilterChainResolverFactory.URLS, "/foobar", "anon")

        def configLoader = createNiceMock(ConfigLoader)
        def filterChainResolverFactory = createNiceMock(Factory)
        def filterChainResolver = createNiceMock(FilterChainResolver)

        expect(filterChainResolverFactory.getInstance()).andReturn(filterChainResolver);

        replay configLoader, filterChainResolverFactory, filterChainResolver

        StormpathShiroIniEnvironment environment = new StormpathShiroIniEnvironment() {
            @Override
            protected Factory<? extends FilterChainResolver> getFilterChainResolverFactory(FilterChainResolver originalFilterChainResolver) {
                return filterChainResolverFactory;
            }

            @Override
            protected ConfigLoader ensureConfigLoader() {
                return configLoader
            }
        };
        environment.setIni(ini)
        environment.setServletContext(servletContext)
        environment.init()

        verify servletContext, filterChainResolverFactory, filterChainResolver

        assertNotNull environment.getFilterChainResolver()
    }

    @Test
    public void testDestroyCleanup() {

        def servletContext = createStrictMock(ServletContext)
        def configLoader = createStrictMock(ConfigLoader)
        servletContext.removeAttribute(Client.getName())
        configLoader.destroyConfig(servletContext)

        replay servletContext, configLoader

        def environment = new StormpathShiroIniEnvironment()
        environment.servletContext = servletContext
        environment.configLoader = configLoader
        environment.destroy()

        verify servletContext, configLoader
    }

    private void doTestWithIni(Ini ini, String expectedApplicationHref, ServletContext servletContext) {

        def configLoader = createNiceMock(ConfigLoader)

        replay configLoader

        StormpathShiroIniEnvironment environment = new StormpathShiroIniEnvironment()
        environment.setIni(ini)
        environment.setServletContext(servletContext)
        environment.configLoader = configLoader
        environment.init()

        DefaultWebSecurityManager securityManager = (DefaultWebSecurityManager) environment.getWebSecurityManager();

        verify servletContext

        assertThat securityManager.getRealms(), allOf(Matchers.contains(any(ApplicationRealm)), hasSize(1))
        ApplicationRealm realm = securityManager.getRealms().iterator().next()
        assertThat realm.getApplicationRestUrl(), equalTo(expectedApplicationHref)

        def clientObject = environment.objects.get("stormpathClient")
        assertThat clientObject, instanceOf(ClientFactory)
        def actualClient = ((ClientFactory) clientObject).getInstance()
        assertSame realm.getClient(), actualClient
    }

    private IExpectationSetters<ServletContext> expectConfigFromServletContext(ServletContext servletContext, final Map<String, ?> delayedInitMap, String configKey = "config") {
        return expect(servletContext.getAttribute(Config.getName())).andAnswer(new IAnswer<Object>() {
            @Override
            Object answer() throws Throwable {
                return delayedInitMap.get(configKey)
            }
        })
    }

}
