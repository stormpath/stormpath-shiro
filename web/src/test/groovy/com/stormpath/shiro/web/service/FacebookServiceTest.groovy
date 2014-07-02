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
package com.stormpath.shiro.web.service

import com.stormpath.sdk.provider.CreateProviderRequest
import com.stormpath.sdk.provider.FacebookCreateProviderRequestBuilder
import com.stormpath.shiro.web.conf.Configuration
import org.junit.Test

import static org.junit.Assert.*;

/**
 * @since 0.6.0
 */
class FacebookServiceTest {

    @Test
    public void testInstantiation() {
        def service = FacebookService.instance;
        assertNotNull(service)
        assertEquals(service.PROVIDER_ID, "facebook")
    }

    @Test
    public void testCreateProviderRequest() {

        def service = FacebookService.instance;
        def request = service.createProviderRequest();
        assertTrue(request instanceof CreateProviderRequest)
        assertEquals(request.provider.toString(), "providerId: facebook, clientId: 237186450762013, clientSecret: b83fde49e2a4f41d4dh6200aae4b840f")
    }
}
