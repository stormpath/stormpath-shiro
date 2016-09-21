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
package com.stormpath.shiro.spring.boot.autoconfigure

import com.stormpath.shiro.spring.boot.autoconfigure.ShiroAutoConfigurationTestApplication.EventBusAwareObject
import com.stormpath.shiro.spring.boot.autoconfigure.ShiroAutoConfigurationTestApplication.SubscribedListener
import org.apache.shiro.authc.UsernamePasswordToken
import org.apache.shiro.event.EventBus
import org.apache.shiro.mgt.DefaultSecurityManager
import org.apache.shiro.mgt.SecurityManager
import org.apache.shiro.subject.Subject
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests
import org.testng.annotations.Test

import static org.testng.Assert.*

/**
 * @since 0.7.0
 */
@SpringBootTest(classes = [ShiroAutoConfigurationTestApplication])
public class ShiroSpringAutoConfigurationTest extends AbstractTestNGSpringContextTests {

    @Autowired
    private SecurityManager securityManager

    @Autowired
    private EventBus eventBus;

    @Autowired
    private EventBusAwareObject eventBusAwareObject;

    @Autowired
    private SubscribedListener subscribedListener;

    @Test
    public void testMinimalConfiguration() {

        // first do a quick check of the injected objects
        assertNotNull securityManager
        assertTrue(securityManager instanceof DefaultSecurityManager)

        assertNotNull eventBusAwareObject
        assertNotNull eventBus
        assertTrue(eventBus.registry.containsKey(subscribedListener))
        assertSame(eventBusAwareObject.getEventBus(), eventBus)
        assertSame(((DefaultSecurityManager)securityManager).getEventBus(), eventBus)


        // now lets do a couple quick permission tests to make sure everything has been initialized correctly.
        Subject joeCoder = new Subject.Builder(securityManager).buildSubject()
        joeCoder.login(new UsernamePasswordToken("joe.coder", "password"))
        joeCoder.checkPermission("read")
        assertTrue joeCoder.hasRole("user")
        assertFalse joeCoder.hasRole("admin")
        joeCoder.logout()
    }


}
