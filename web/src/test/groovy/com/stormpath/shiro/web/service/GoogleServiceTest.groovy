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
import org.junit.Test

import static org.junit.Assert.*

/**
 * @since 0.7.0
 */
class GoogleServiceTest {

    @Test
    public void testInstantiation() {
        def service = GoogleService.instance;
        assertNotNull(service)
        assertEquals(service.PROVIDER_ID, "google")
    }

    @Test
    public void testCreateProviderRequest() {

        def service = GoogleService.instance;
        def request = service.createProviderRequest();
        assertTrue(request instanceof CreateProviderRequest)
        assertEquals(request.provider.toString(), "providerId: google, clientId: 113482914701.apps.googleusercontent.com," +
                " clientSecret: A-IllodaywOn1_3M4QooulPj, redirectUri: http://localhost:8080/googleOauthCallback")
    }
}
