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

import com.stormpath.sdk.application.Application
import com.stormpath.sdk.client.Client
import com.stormpath.shiro.realm.StormpathWebRealm
import org.apache.shiro.mgt.SecurityManager
import org.apache.shiro.web.mgt.WebSecurityManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests
import org.testng.annotations.Test

import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.*
import static org.testng.Assert.assertNotNull

@SpringBootTest(classes = [StormpathShiroWebAutoConfigurationTestApplication])
public class StormpathShiroWebSpringAutoConfigurationTest extends AbstractTestNGSpringContextTests {

    @Autowired
    private SecurityManager securityManager

    @Autowired
    Client client

    @Autowired
    Application application

    @Test
    public void testMinimalConfiguration() {

        assertNotNull securityManager
        assertThat securityManager, instanceOf(WebSecurityManager)
        assertNotNull client
        assertNotNull application

        assertThat securityManager.realms, allOf(hasSize(1), hasItem(instanceOf(StormpathWebRealm)))
    }
}
