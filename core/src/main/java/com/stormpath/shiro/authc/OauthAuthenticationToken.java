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
package com.stormpath.shiro.authc;

import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.provider.ProviderAccountRequest;
import org.apache.shiro.authc.AuthenticationToken;

/**
 * @since 0.6.0
 */
public abstract class OauthAuthenticationToken implements AuthenticationToken {

    protected final String token;

    public OauthAuthenticationToken(String token) {
        Assert.notNull(token, "token cannot be null.");
        this.token = token;
    }

    @Override
    public Object getPrincipal() {
        return null;
    }

    @Override
    public Object getCredentials() {
        return getProviderAccountRequest();
    }

    protected abstract ProviderAccountRequest getProviderAccountRequest();
}
