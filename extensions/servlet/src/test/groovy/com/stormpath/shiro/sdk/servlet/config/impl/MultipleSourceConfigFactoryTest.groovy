package com.stormpath.shiro.sdk.servlet.config.impl

import com.stormpath.sdk.servlet.config.impl.DefaultConfigFactory
import org.testng.annotations.Test

import javax.servlet.ServletContext

import static org.easymock.EasyMock.*
import static org.testng.Assert.*

/**
 * Tests for {@link MultipleSourceConfigFactory}.
 */
class MultipleSourceConfigFactoryTest {

    @Test
    public void testDefaultCreate() {

        def servletConfig = createMock(ServletContext)

        expect(servletConfig.getInitParameter(DefaultConfigFactory.STORMPATH_PROPERTIES_SOURCES)).andReturn(null)
        expect(servletConfig.getInitParameter(DefaultConfigFactory.STORMPATH_PROPERTIES)).andReturn(null)
        expect(servletConfig.getResourceAsStream(anyObject(String))).andReturn(null).times(4)

        replay servletConfig

        def configFactory = new MultipleSourceConfigFactory()
        assertNotNull configFactory.createConfig(servletConfig)

        verify servletConfig
    }

}
