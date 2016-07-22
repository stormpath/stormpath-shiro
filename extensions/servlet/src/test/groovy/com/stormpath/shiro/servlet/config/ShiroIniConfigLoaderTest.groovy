package com.stormpath.shiro.servlet.config

import com.stormpath.sdk.servlet.config.Config
import com.stormpath.sdk.servlet.config.ConfigLoader
import com.stormpath.sdk.servlet.i18n.MessageContext
import org.easymock.Capture
import org.testng.annotations.Test

import javax.servlet.ServletContext

import static org.easymock.EasyMock.*

/**
 * Tests for {@link ShiroIniConfigLoader}.
 */
public class ShiroIniConfigLoaderTest {

    @Test
    public void testDestroyCleanup() {

        def servletContext = createMock(ServletContext)

        servletContext.log(anyObject(String))
        expectLastCall().anyTimes()

        servletContext.removeAttribute(Config.getName())
        servletContext.removeAttribute(MessageContext.class.getName())
        servletContext.removeAttribute(StormpathShiroConfigFactory.SHIRO_STORMPATH_PROPERTIES_ATTRIBUTE)

        replay servletContext

        def configLoader = new ShiroIniConfigLoader(null)
        configLoader.destroyConfig(servletContext)

        verify servletContext
    }

    @Test
    public void testCreateConfig() {

        def servletContext = createMock(ServletContext)
        def configCapture = new Capture<Config>()

        servletContext.log(anyObject(String))
        expectLastCall().anyTimes()

        expect(servletContext.getInitParameter(ConfigLoader.CONFIG_FACTORY_CLASS_PARAM_NAME)).andReturn(null)
        expect(servletContext.setInitParameter(ConfigLoader.CONFIG_FACTORY_CLASS_PARAM_NAME, StormpathShiroConfigFactory.getName())).andReturn(true)

        // we verified the correct config class was set, now return a different one for testing
        expect(servletContext.getInitParameter(ConfigLoader.CONFIG_FACTORY_CLASS_PARAM_NAME)).andReturn(MockConfigFactory.getName())
        servletContext.setAttribute(eq(StormpathShiroConfigFactory.SHIRO_STORMPATH_PROPERTIES_ATTRIBUTE), anyObject(IniPropertiesSource))
        expect(servletContext.getAttribute(Config.getName())).andReturn(null)
        servletContext.setAttribute(eq(Config.getName()), capture(configCapture))
        servletContext.setAttribute(MessageContext.getName(), null)

        replay servletContext

        def configLoader = new ShiroIniConfigLoader(null)
        configLoader.createConfig(servletContext)

        verify servletContext
    }

}
