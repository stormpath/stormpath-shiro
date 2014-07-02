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

import com.stormpath.shiro.web.service.ProviderService;
import com.stormpath.shiro.web.utils.Constants;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.web.filter.authc.PassThruAuthenticationFilter;
import org.apache.shiro.web.util.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * A {@link PassThruAuthenticationFilter} providing authentication via Oauth providers like
 * Facebook or Google.
 * <p/>
 * This abstract class provides most of the Oauth authentication logic and relies on Provider-specific
 * sub-classes (like {@link FacebookFilter} or {@link GoogleFilter}) to fulfill its task.
 *
 * @since 0.6.0
 */
public abstract class OauthFilter extends PassThruAuthenticationFilter {
    private static final Logger logger = LoggerFactory.getLogger(OauthFilter.class);

    private final ProviderService providerService;

    protected OauthFilter(ProviderService providerManager) {
        this.providerService = providerManager;
    }

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws IOException {
        String code = WebUtils.getCleanParam(request, "code");
        if(code != null && isAccessOK(code)) {
            WebUtils.redirectToSavedRequest(request, response, getSuccessUrl());
            return false;
        }

        //If we are here it is very likely that the Provider-based Account Store has not yet being created in
        //Stormpath. Let's make developer's life easier and let's try to create it automatically for him...
        if (!providerService.hasProviderBasedAccountStore()) {
            //Indeed, the application does not have a Provider-based Account Store.
            providerService.createProviderAccountStore();
            //Let's re-try to login
            if(isAccessOK(code)) {
                WebUtils.redirectToSavedRequest(request, response, getSuccessUrl());
                return false;
            }
        }

        //The Provider-based directory already existed in the Stormpath Application as an account store but the attempt to login
        //through it did not succeed. There is nothing else we can do, there is indeed an error when trying to login via
        //this Oauth provider.
        Map<String, String> failureMap = new HashMap<String, String>();
        failureMap.put(Constants.MESSAGE_FLAG, this.providerService.PROVIDER_ID + Constants.LOGIN_ERROR);
        WebUtils.issueRedirect(request, response, getLoginUrl(), failureMap);
        return true;
    }

    /**
     * Tries to login using the given code. Delegates the creation of the {@link org.apache.shiro.authc.AuthenticationToken} to the
     * Provider-specific implementation.
     *
     * @param code the actual token received from the Oauth provider.
     * @return true if the login succeeded; false otherwise.
     */
    protected boolean isAccessOK(String code) {
        try {
            SecurityUtils.getSubject().login(getOauthAuthenticatingToken(code));
            return true;
        } catch (AuthenticationException e) {
            logger.debug("The Oauth user cannot access this application.");
        }
        return false;
    }

    /**
     * Creates a new Provider-specific {@link org.apache.shiro.authc.AuthenticationToken} using the received authorization code from the provider.
     * @param code the authorization code received from the provider.
     * @return a new Provider-specific {@link org.apache.shiro.authc.AuthenticationToken} using the received authorization accessToken.
     */
    protected abstract AuthenticationToken getOauthAuthenticatingToken(String code);

}
