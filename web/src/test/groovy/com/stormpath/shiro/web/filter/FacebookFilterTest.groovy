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
package com.stormpath.shiro.web.filter

import com.stormpath.shiro.authc.FacebookAuthenticationToken
import org.junit.Test

/**
 * @since 0.6.0
 */
class FacebookFilterTest {

    @Test
    public void testToken() {
        def facebookFilter = new FacebookFilter()
        def token = facebookFilter.getOauthAuthenticatingToken("someFacebookCode")
        assert(token instanceof FacebookAuthenticationToken)
        token.principal.equals("someFacebookCode")
    }

}
