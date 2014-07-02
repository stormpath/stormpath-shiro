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
package com.stormpath.shiro.web.filter;

import com.stormpath.shiro.authc.FacebookAuthenticationToken;
import com.stormpath.shiro.web.service.FacebookService;
import org.apache.shiro.authc.AuthenticationToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Facebook-specific {@link OauthFilter}. The Facebook redirectUri must be configured to be handled by this Filter. For example,
 * in config.ini:
 * <pre>
 *     [Facebook]
 *      appId = ***your_facebook_app_id***
 *      appSecret = ***your_facebook_app_secret***
 *      redirectUri = http://localhost:8080/facebookOauthCallback
 *      scope = email
 * </pre>
 * and in shiro.ini:
 * <pre>
 *      [main]
 *      facebookOauth = com.stormpath.shiro.web.filter.FacebookFilter
 *      #...
 *      [urls]
 *      /facebookOauthCallback = facebookOauth
 * </pre>
 *
 * @since 0.6.0
 */
public class FacebookFilter extends OauthFilter {

    private static final Logger logger = LoggerFactory.getLogger(FacebookFilter.class);

    public FacebookFilter() {
        super(FacebookService.getInstance());
    }

    /**
     * Creates a new {@link com.stormpath.shiro.authc.FacebookAuthenticationToken} using the received authorization accessToken.
     * @param code the authorization accessToken received from Facebook.
     * @return a new {@link com.stormpath.shiro.authc.FacebookAuthenticationToken} using the received authorization accessToken.
     */
    @Override
    protected AuthenticationToken getOauthAuthenticatingToken(String code) {
        return new FacebookAuthenticationToken(code);
    }

}
