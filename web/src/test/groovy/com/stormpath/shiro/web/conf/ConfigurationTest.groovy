/*
 * Copyright 2014 Stormpath, Inc.
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
package com.stormpath.shiro.web.conf;

import org.junit.Test

import static org.junit.Assert.*;

/**
 * @since 0.6.0
 */
class ConfigurationTest {

    @Test
    public void testOverride() {

        assertEquals(Configuration.ini.size(), 5)
        assertNotNull(Configuration.ini.getSection("App"))
        assertEquals(Configuration.ini.getSectionProperty("App", "baseUrl"), "http://localhost:8080")
        assertTrue(Configuration.isGoogleEnabled())
        assertTrue(Configuration.isFacebookEnabled())
        assertEquals(Configuration.getFacebookAppId(), "237186450762013")
        assertEquals(Configuration.getFacebookAppSecret(), "b83fde49e2a4f41d4dh6200aae4b840f")
        assertEquals(Configuration.getFacebookRedirectUri(), "http://localhost:8080/facebookOauthCallback")
        assertEquals(Configuration.getFacebookScope(), "email")
        assertEquals(Configuration.getGoogleClientId(), "113482914701.apps.googleusercontent.com")
        assertEquals(Configuration.getGoogleClientSecret(), "A-IllodaywOn1_3M4QooulPj")
        assertEquals(Configuration.ini.getSectionProperty("NewSection", "someKey"), "someValue")

    }
}
