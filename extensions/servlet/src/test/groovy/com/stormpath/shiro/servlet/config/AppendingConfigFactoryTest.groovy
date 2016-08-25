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

import com.stormpath.sdk.impl.config.PropertiesSource
import com.stormpath.sdk.servlet.config.impl.DefaultConfigFactory
import com.stormpath.shiro.servlet.config.AppendingConfigFactory
import org.apache.shiro.config.ConfigurationException
import org.testng.annotations.Test

import javax.servlet.ServletContext

import static org.easymock.EasyMock.*
import static org.testng.Assert.assertEquals
import static org.testng.Assert.assertNotNull
import static org.testng.Assert.fail

/**
 * Tests for {@link AppendingConfigFactory}.
 * @since 0.7.0
 */
class AppendingConfigFactoryTest {

    @Test
    public void testDefaultCreate() {

        def servletContext = createMock(ServletContext)

        expect(servletContext.getInitParameter(DefaultConfigFactory.STORMPATH_PROPERTIES_SOURCES)).andReturn(null)
        expect(servletContext.getInitParameter(DefaultConfigFactory.STORMPATH_PROPERTIES)).andReturn(null)
        expect(servletContext.getResourceAsStream(anyObject(String))).andReturn(null).anyTimes()

        expect(servletContext.getAttribute(AppendingConfigFactory.SHIRO_STORMPATH_ADDITIONAL_PROPERTIES_ATTRIBUTE)).andReturn(null)

        replay servletContext

        def configFactory = new AppendingConfigFactory()
        assertNotNull configFactory.createConfig(servletContext)

        verify servletContext
    }

    @Test
    public void testLoadingPropertiesSourceFromAttribute() {

        def servletContext = createMock(ServletContext)

        def propertiesSource = createMock(PropertiesSource)
        expect(propertiesSource.properties).andReturn([testProperty1:'ONE', testProperty2:'TWO'])

        expect(servletContext.getInitParameter(DefaultConfigFactory.STORMPATH_PROPERTIES_SOURCES)).andReturn(null)
        expect(servletContext.getInitParameter(DefaultConfigFactory.STORMPATH_PROPERTIES)).andReturn(null)
        expect(servletContext.getResourceAsStream(anyObject(String))).andReturn(null).anyTimes()

        expect(servletContext.getAttribute(AppendingConfigFactory.SHIRO_STORMPATH_ADDITIONAL_PROPERTIES_ATTRIBUTE))
                .andReturn(Collections.singleton(propertiesSource))

        replay servletContext, propertiesSource

        def configFactory = new AppendingConfigFactory()
        def config = configFactory.createConfig(servletContext)
        assertNotNull config
        assertEquals(config.get("testProperty1"), "ONE")
        assertEquals(config.get("testProperty2"), "TWO")

        verify servletContext, propertiesSource
    }

    @Test
    public void testNonCollectionValue() {

        def servletContext = createMock(ServletContext)

        expect(servletContext.getInitParameter(DefaultConfigFactory.STORMPATH_PROPERTIES_SOURCES)).andReturn(null)
        expect(servletContext.getInitParameter(DefaultConfigFactory.STORMPATH_PROPERTIES)).andReturn(null)
        expect(servletContext.getResourceAsStream(anyObject(String))).andReturn(null).anyTimes()

        expect(servletContext.getAttribute(AppendingConfigFactory.SHIRO_STORMPATH_ADDITIONAL_PROPERTIES_ATTRIBUTE)).andReturn(new Object())

        replay servletContext

        def configFactory = new AppendingConfigFactory()

        try {
            configFactory.createConfig(servletContext)
            fail "Expected ConfigurationException"
        }
        catch(ConfigurationException e) {
            // expected
        }

        verify servletContext
    }

}
