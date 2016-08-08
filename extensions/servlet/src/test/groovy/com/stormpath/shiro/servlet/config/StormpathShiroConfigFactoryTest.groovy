package com.stormpath.shiro.servlet.config

import com.stormpath.sdk.impl.config.PropertiesSource
import org.testng.annotations.Test

import javax.servlet.ServletContext

import static org.easymock.EasyMock.*
import static org.hamcrest.MatcherAssert.*
import static org.hamcrest.Matchers.*

/**
 * Tests for {@link StormpathShiroConfigFactory}.
 */
class StormpathShiroConfigFactoryTest {

    @Test
    public void testGetPropertySourcesAttributeNotSet() {

        def servletContext = createMock(ServletContext)

        expect(servletContext.getAttribute(StormpathShiroConfigFactory.SHIRO_STORMPATH_PROPERTIES_ATTRIBUTE)).andReturn(null)

        replay servletContext

        def configFactory = new StormpathShiroConfigFactory()
        def propertiesSources = configFactory.getInternalPropertiesSources(servletContext)

        verify servletContext

        assertThat propertiesSources, is(empty())
    }

    @Test
    public void testGetPropertySourcesWithSource() {

        def servletContext = createMock(ServletContext)
        def propertiesSource = createMock(PropertiesSource)

        expect(servletContext.getAttribute(StormpathShiroConfigFactory.SHIRO_STORMPATH_PROPERTIES_ATTRIBUTE)).andReturn(propertiesSource)

        replay servletContext, propertiesSource

        def configFactory = new StormpathShiroConfigFactory()
        def propertiesSources = configFactory.getInternalPropertiesSources(servletContext)

        verify servletContext, propertiesSource

        assertThat propertiesSources, hasSize(1)

    }

}
