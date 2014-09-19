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
package com.stormpath.shiro.servlet.service;

import com.stormpath.sdk.idsite.IdSiteCallbackHandler;
import com.stormpath.sdk.idsite.IdSiteResultListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;

/**
 * This singleton service provides ID Site-related operations such as creating the <code>redirectUri</code> for both login and
 * logout. This service is used by the {@link com.stormpath.shiro.servlet.http.IdSiteServlet IDSiteServlet}.
 *
 * @since 0.7.0
 */
public class IdSiteService extends AbstractService {
    private static final Logger logger = LoggerFactory.getLogger(IdSiteService.class);

    private static IdSiteService service = null;

    /**
     * Let's make the constructor private so we can have a single IDSiteService.
     */
    private IdSiteService() {
    }

    public static IdSiteService getInstance() {
        if(service == null) {
            service = new IdSiteService();
        }
        return service;
    }

    /**
     * Returns the <code>redirectUri</code> to be used when logging in via ID Site.
     *
     * @param redirectUri the <code>redirectUri</code> that will be used to login via ID Site.
     * @return the <code>redirectUri</code> to be used when logging in via ID Site.
     */
    public String getLoginRedirectUri(String redirectUri) {
        return getStormpathApplication().newIdSiteUrlBuilder().setCallbackUri(redirectUri).build();
    }

    /**
     * Returns the <code>redirectUri</code> to be used when logging out via ID Site.
     *
     * @param redirectUri the <code>redirectUri</code> that will be used to logout via ID Site.
     * @return the <code>redirectUri</code> to be used when logging out via ID Site.
     */
    public String getLogoutRedirectUri(String redirectUri) {
        return getStormpathApplication().newIdSiteUrlBuilder().setCallbackUri(redirectUri).forLogout().build();
    }

    /**
     * Returns a new {@link com.stormpath.sdk.idsite.IdSiteCallbackHandler} used to handle HTTP replies from your ID Site to your
     * application's {@code callbackUri}. This <code>IdSiteCallbackHandler</code> will have the given
     * {@link com.stormpath.sdk.idsite.IdSiteResultListener IdSiteResultListener} associated to it. This listener
     * will be notified about the ID-site executed operation.
     *
     * @param request the {@code javax.servlet.http.HttpServletRequest} instance.
     * @param idSiteResultListener the {@link com.stormpath.sdk.idsite.IdSiteResultListener} that will be notified about the actual operation of the ID Site
     *                       invocation: registration, authentication or logout. If <code>resultListener<code/> is null, no notification
     *                       will be sent.
     * @return an {@link com.stormpath.sdk.idsite.IdSiteCallbackHandler} that allows to customize how the {@code httpRequest} will be handled.
     */
    public IdSiteCallbackHandler getCallbackHandler(HttpServletRequest request, IdSiteResultListener idSiteResultListener) {
        return getStormpathApplication().newIdSiteCallbackHandler(request).setResultListener(idSiteResultListener);
    }

}
