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
package com.stormpath.shiro.servlet.conf

import org.junit.Assert;
import org.junit.Test

import static org.junit.Assert.*;

/**
 * @since 0.7.0
 */
class ConfigurationTest {

    @Test
    public void testOverride() {
        Assert.assertEquals(3, Configuration.ini.size())
        assertNotNull(Configuration.ini.getSection("App"))
        assertEquals(Configuration.ini.getSectionProperty("App", "baseUrl"), "http://localhost:8080")
        assertTrue(Configuration.IDSiteEnabled)
        assertEquals(Configuration.loginRedirectUri, "http://localhost:8080/index.jsp")
        assertEquals(Configuration.logoutRedirectUri, "http://localhost:8080/index.html")
    }
}
