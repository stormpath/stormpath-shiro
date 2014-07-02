/*
 * Copyright 2013 Stormpath, Inc.
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
package com.stormpath.shiro.authc

import com.stormpath.sdk.provider.FacebookProviderData
import com.stormpath.sdk.provider.ProviderAccountRequest
import org.junit.Test

import static org.junit.Assert.*

/**
 * @since 0.6.0
 */
class FacebookAuthenticationTokenTest {

    @Test
    void test() {
        def token = new FacebookAuthenticationToken("fooFacebookCode")
        def request = token.providerAccountRequest
        assertEquals("facebook", request.providerData.providerId)
        assertEquals("fooFacebookCode", ((FacebookProviderData)((ProviderAccountRequest)token.getCredentials()).getProviderData()).accessToken)
        assertNull(token.getPrincipal())
    }

}
