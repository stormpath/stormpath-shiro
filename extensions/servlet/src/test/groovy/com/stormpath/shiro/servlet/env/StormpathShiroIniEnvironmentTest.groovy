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
package com.stormpath.shiro.servlet.env

import com.stormpath.sdk.client.Client
import com.stormpath.sdk.servlet.config.Config
import com.stormpath.sdk.servlet.config.ConfigLoader
import com.stormpath.sdk.servlet.config.impl.DefaultConfigFactory
import com.stormpath.shiro.servlet.config.ClientFactory
import com.stormpath.shiro.realm.ApplicationRealm
import com.stormpath.shiro.servlet.ShiroTestSupportWithSystemProperties
import com.stormpath.shiro.stubs.StubApplicationResolver
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
 * @since 0.7.0
 */
@Test(singleThreaded = true)
class StormpathShiroIniEnvironmentTest extends ShiroTestSupportWithSystemProperties {

    @Test
    public void testDefaultCreate() {

        def servletContext = mock(ServletContext)

        def clientCapture = new Capture<Client>();

        final def delayedInitMap = new HashMap<String, Object>()
        final def configKey = "config"

        expectConfigFromServletContext(servletContext, delayedInitMap, configKey).anyTimes()

        expect(servletContext.getInitParameter(DefaultConfigFactory.STORMPATH_PROPERTIES_SOURCES)).andReturn(null).times(2)
        expect(servletContext.setInitParameter(DefaultConfigFactory.STORMPATH_PROPERTIES_SOURCES, StormpathShiroIniEnvironment.SHIRO_STORMPATH_PROPERTIES_SOURCES)).andReturn(true)
        expect(servletContext.getInitParameter(DefaultConfigFactory.STORMPATH_PROPERTIES)).andReturn(null)
        expect(servletContext.getResourceAsStream(anyObject())).andReturn(null).anyTimes()
        servletContext.setAttribute(eq(Client.getName()), capture(clientCapture))

        replay servletContext

        def config = new DefaultConfigFactory().createConfig(servletContext)
        delayedInitMap.put(configKey, config)


        def ini = new Ini()
        doTestWithIni(ini, servletContext, config)
    }

    @Test
    public void testDefaultAddedToIni() {

        def environment = new StormpathShiroIniEnvironment()
        environment.setIni(null)

        def ini = environment.getIni()
        assertNotNull ini
        assertThat(ini.getSection(Ini.DEFAULT_SECTION_NAME), (allOf(hasEntry("shiro.loginUrl", "/login"),
                                                                    hasEntry("stormpathRealm.client", "\$stormpathClient"))))
    }

    @Test
    public void testCreateWithIniAppHref() {

        def servletContext = mock(ServletContext)

        def clientCapture = new Capture<Client>();

        final def delayedInitMap = new HashMap<String, Object>()
        final def configKey = "config"

        expectConfigFromServletContext(servletContext, delayedInitMap, configKey).anyTimes()

        expect(servletContext.getInitParameter(DefaultConfigFactory.STORMPATH_PROPERTIES_SOURCES)).andReturn(null).times(2)
        expect(servletContext.setInitParameter(DefaultConfigFactory.STORMPATH_PROPERTIES_SOURCES, StormpathShiroIniEnvironment.SHIRO_STORMPATH_PROPERTIES_SOURCES)).andReturn(true)

        expect(servletContext.getInitParameter(DefaultConfigFactory.STORMPATH_PROPERTIES)).andReturn(null)
        expect(servletContext.getResourceAsStream(anyObject())).andReturn(null).anyTimes()
        servletContext.setAttribute(eq(Client.getName()), capture(clientCapture))

        replay servletContext

        def config = new DefaultConfigFactory().createConfig(servletContext)
        delayedInitMap.put(configKey, config)

        def ini = new Ini()

        doTestWithIni(ini, servletContext, config)
    }


    @Test
    public void testSimpleFilterConfig() {

        def servletContext = mock(ServletContext)

        def clientCapture = new Capture<Client>();

        final def delayedInitMap = new HashMap<String, Object>()
        final def configKey = "config"

        expectConfigFromServletContext(servletContext, delayedInitMap, configKey).anyTimes()

        expect(servletContext.getInitParameter(DefaultConfigFactory.STORMPATH_PROPERTIES_SOURCES)).andReturn(null).times(2)
        expect(servletContext.setInitParameter(DefaultConfigFactory.STORMPATH_PROPERTIES_SOURCES, StormpathShiroIniEnvironment.SHIRO_STORMPATH_PROPERTIES_SOURCES)).andReturn(true)
        expect(servletContext.getInitParameter(DefaultConfigFactory.STORMPATH_PROPERTIES)).andReturn(null)
        expect(servletContext.getResourceAsStream(anyObject())).andReturn(null).anyTimes()
        servletContext.setAttribute(eq(Client.getName()), capture(clientCapture))

        replay servletContext

        def config = new DefaultConfigFactory().createConfig(servletContext)
        delayedInitMap.put(configKey, config)

        def ini = new Ini()
        addStubApplicationResolvertoIni(ini)
        // we need to have at least one path defined for the filterChain to be configured.
        ini.setSectionProperty(IniFilterChainResolverFactory.URLS, "/foobar", "anon")

        def configLoader = createMock(ConfigLoader)
        def filterChainResolverFactory = createNiceMock(Factory)
        def filterChainResolver = createNiceMock(FilterChainResolver)

        expect(filterChainResolverFactory.getInstance()).andReturn(filterChainResolver);
        expect(configLoader.createConfig(servletContext)).andReturn(config)

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

    private void doTestWithIni(Ini ini, ServletContext servletContext, Config config) {

        addStubApplicationResolvertoIni(ini)

        def configLoader = createMock(ConfigLoader)
        expect(configLoader.createConfig(servletContext)).andReturn(config)

        replay configLoader //, app, appResolver

        StormpathShiroIniEnvironment environment = new StormpathShiroIniEnvironment()
        environment.setIni(ini)
        environment.setServletContext(servletContext)
        environment.configLoader = configLoader
        environment.init()

        DefaultWebSecurityManager securityManager = (DefaultWebSecurityManager) environment.getWebSecurityManager();

        verify servletContext //, app, appResolver

        assertThat securityManager.getRealms(), allOf(Matchers.contains(any(ApplicationRealm)), hasSize(1))
        ApplicationRealm realm = securityManager.getRealms().iterator().next()

        def clientObject = environment.objects.get("stormpathClient")
        assertThat clientObject, instanceOf(ClientFactory)
        def actualClient = ((ClientFactory) clientObject).getInstance()
        assertSame realm.getClient(), actualClient
    }

    private void addStubApplicationResolvertoIni(Ini ini, String sectionName = "main") {
        assertNotNull ini
        ini.setSectionProperty(sectionName, "stubApplicationResolver", StubApplicationResolver.getName())
        ini.setSectionProperty(sectionName, "stormpathRealm.applicationResolver", "\$stubApplicationResolver")
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
