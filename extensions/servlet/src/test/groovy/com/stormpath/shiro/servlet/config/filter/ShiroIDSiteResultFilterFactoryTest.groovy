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
package com.stormpath.shiro.servlet.config.filter

import com.stormpath.sdk.servlet.config.Config
import com.stormpath.sdk.servlet.filter.ControllerConfig
import com.stormpath.shiro.servlet.ShiroTestSupport
import com.stormpath.shiro.servlet.mvc.ShiroIDSiteResultController
import com.stormpath.shiro.servlet.mvc.ShiroLogoutController
import org.testng.annotations.Test

import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.instanceOf

import static org.easymock.EasyMock.*

/**
 * Tests for {@link ShiroIDSiteResultFilterFactory}.
 */
class ShiroIDSiteResultFilterFactoryTest extends ShiroTestSupport {

    @Test
    public void testNewController() {

        def config = createMock(Config)
        def logoutConfig = createMock(ControllerConfig)
        def registerConfig = createMock(ControllerConfig)
        def factory = new ShiroIDSiteResultFilterFactory()
        def controller = factory.newController()

        expect(config.getLogoutConfig()).andReturn(logoutConfig)
        expect(logoutConfig.getNextUri()).andReturn("logout-nextUri")
        expect(config.isLogoutInvalidateHttpSession()).andReturn(true)
        expect(config.getProducedMediaTypes()).andReturn(Collections.emptyList())
        expect(config.getRegisterConfig()).andReturn(registerConfig)
        expect(registerConfig.getNextUri()).andReturn("register-nextUri")

        replay config, logoutConfig, registerConfig

        factory.doConfigure(controller, config)

        assertThat controller, instanceOf(ShiroIDSiteResultController)
        assertThat controller.logoutController, instanceOf(ShiroLogoutController)

        verify config, logoutConfig, registerConfig
    }
}
