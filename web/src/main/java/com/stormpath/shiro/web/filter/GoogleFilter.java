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

import com.stormpath.shiro.authc.GoogleAuthenticationToken;
import com.stormpath.shiro.web.service.GoogleService;
import org.apache.shiro.authc.AuthenticationToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Google-specific {@link OauthFilter}. The Google redirectUri must be configured to be handled by this Filter. For example,
 * in config.ini:
 * <pre>
 *     [Google]
 *     clientId = ***your_google_client_id***
 *     clientSecret = ***your_google_client_secret***
 *     redirectUri = http://localhost:8080/googleOauthCallback
 * </pre>
 * and in shiro.ini:
 * <pre>
 *      [main]
 *      googleOauth = com.stormpath.shiro.web.filter.GoogleFilter
 *      #...
 *      [urls]
 *      /googleOauthCallback = googleOauth
 * </pre>
 *
 * @since 0.6.0
 */
public class GoogleFilter extends OauthFilter {
    private static final Logger logger = LoggerFactory.getLogger(GoogleFilter.class);

    public GoogleFilter() {
        super(GoogleService.getInstance());
    }

    /**
     * Creates a new {@link com.stormpath.shiro.authc.GoogleAuthenticationToken} using the received authorization code.
     * @param code the authorization code received from Google.
     * @return a new {@link com.stormpath.shiro.authc.GoogleAuthenticationToken} using the received authorization code.
     */
    @Override
    protected AuthenticationToken getOauthAuthenticatingToken(String code) {
        return new GoogleAuthenticationToken(code);
    }


}
