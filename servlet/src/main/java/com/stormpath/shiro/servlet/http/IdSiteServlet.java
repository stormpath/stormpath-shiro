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
package com.stormpath.shiro.servlet.http;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.idsite.IdSiteCallbackHandler;
import com.stormpath.sdk.idsite.IdSiteResultListener;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.shiro.authc.IdSiteAccountIDField;
import com.stormpath.shiro.authc.IdSiteAuthenticationToken;
import com.stormpath.shiro.servlet.conf.Configuration;
import com.stormpath.shiro.servlet.conf.UrlFor;
import com.stormpath.shiro.servlet.service.IdSiteService;
import org.apache.shiro.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * This servlet is in charge of handling all the communication with Stormpath's IDSite.
 * </p>
 * Thanks to Servlet 3.0's web fragment feature, this servlet is auto-registered in the Shiro Web Application where this
 * project is being used. Therefore, the developer does not need to do anything to get this Servlet live. Having this
 * project as a dependency in your own Shiro Web application is enough to have it working.
 *
 * @since 0.7.0
 */
@WebListener
public class IdSiteServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(IdSiteServlet.class);

    private static IdSiteResultListener idSiteResultListener = null;
    private IdSiteAccountIDField idSitePrincipalAccountIdField = IdSiteAccountIDField.EMAIL;

    // This servlet will only handle requests for these URIs
    private static final String IDSITE_LOGIN_ACTION = UrlFor.get("idsite_login.action");
    private static final String IDSITE_LOGOUT_ACTION = UrlFor.get("idsite_logout.action");

    private static final String IDSITE_LOGIN_CALLBACK_ACTION = UrlFor.get("idsite_login_callback.action");
    private static final String IDSITE_LOGIN_REDIRECT_URL = Configuration.getBaseUrl() + IDSITE_LOGIN_CALLBACK_ACTION;

    private static final String IDSITE_LOGOUT_CALLBACK_ACTION = UrlFor.get("idsite_logout_callback.action");
    private static final String IDSITE_LOGOUT_REDIRECT_URL = Configuration.getBaseUrl() + IDSITE_LOGOUT_CALLBACK_ACTION;

    /**
     * All ID Site-related requests are handled here relying on the {@link com.stormpath.shiro.servlet.service.IdSiteService} and {@link com.stormpath.shiro.realm.ApplicationRealm
     * ApplicationRealm} to do all the work.
     *
     * @param request  an {@link javax.servlet.http.HttpServletRequest} object that contains the request the client has made of the servlet.
     * @param response an {@link javax.servlet.http.HttpServletResponse} object that contains the response the servlet sends to the client.
     * @throws java.io.IOException            if an input or output error is detected when the servlet handles the POST request
     * @throws javax.servlet.ServletException if the request for the POST could not be handled
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String uri = request.getRequestURI();

        if (uri.startsWith(IDSITE_LOGIN_ACTION)) {
            //Displays the ID Site login screen
            processLogin(response);
        } else if (uri.startsWith(IDSITE_LOGIN_CALLBACK_ACTION)) {
            //Instructs Shiro about the actual account login, updating the security context and setting all account's permissions.
            processLoginCallback(request, response);
        } else if (uri.startsWith(IDSITE_LOGOUT_ACTION)) {
            //Sends the logout to ID Site
            processLogout(response);
        } else if (uri.startsWith(IDSITE_LOGOUT_CALLBACK_ACTION)) {
            //Instructs Shiro about the actual account logout, clearing the security context.
            processLogoutCallback(request, response);
        }
    }

    protected void processLogin(HttpServletResponse response) throws ServletException, IOException {
        //Perform login via ID Site
        logger.debug("Redirecting to the following IDSite Redirect URL: " + IDSITE_LOGIN_REDIRECT_URL);
        addIDSiteHeader(response);
        String callbackUri = IdSiteService.getInstance().getLoginRedirectUri(IDSITE_LOGIN_REDIRECT_URL);
        response.sendRedirect(callbackUri);
    }

    protected void processLoginCallback(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        IdSiteCallbackHandler callbackHandler = IdSiteService.getInstance().getCallbackHandler(request, getIdSiteResultListener());
        //At this point, the IdSiteResultListener will be notified about the login
        Account account = callbackHandler.getAccountResult().getAccount();
        SecurityUtils.getSubject().login(new IdSiteAuthenticationToken(getIdSitePrincipalValue(account), account));
        response.sendRedirect(Configuration.getLoginRedirectUri());
    }

    protected void processLogout(HttpServletResponse response) throws ServletException, IOException {
        logger.debug("Redirecting to the following IDSite Redirect URL: " + IDSITE_LOGOUT_REDIRECT_URL);
        addIDSiteHeader(response);
        String callbackUri = IdSiteService.getInstance().getLogoutRedirectUri(IDSITE_LOGOUT_REDIRECT_URL);
        response.sendRedirect(callbackUri);
    }

    protected void processLogoutCallback(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        SecurityUtils.getSubject().logout();
        IdSiteCallbackHandler callbackHandler = IdSiteService.getInstance().getCallbackHandler(request, getIdSiteResultListener());
        //We need to invoke this operation in order for the IdSiteResultListener to be notified about the logout
        callbackHandler.getAccountResult();
        response.sendRedirect(Configuration.getLogoutRedirectUri());
    }

    private void addIDSiteHeader(HttpServletResponse response) {
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, post-check=0, pre-check=0");
        response.setHeader("Pragma", "no-cache");
    }

    public void setIdSiteResultListener(IdSiteResultListener listener) {
        idSiteResultListener = listener;
    }

    public IdSiteResultListener getIdSiteResultListener() {
        return idSiteResultListener;
    }

    /**
     * Configures the field that will be set as the principal when creating the {@link org.apache.shiro.authc.AuthenticationToken authentication token}
     * after a successful ID Site login.
     * <p/>
     * When users login via ID Site, we do not have access to the actual login information. Thus, we do not know whether the
     * user logged in with his username or his email. Via this field, the developer can configure whether the principal information
     * will be either the {@link com.stormpath.shiro.authc.IdSiteAccountIDField#USERNAME account username} or the {@link com.stormpath.shiro.authc.IdSiteAccountIDField#EMAIL account email}.
     * <p/>
     * By default, the account `email` is used.
     *
     * @param idField either `username` or `email` to express the desired principal to set when constructing the
     * {@link org.apache.shiro.authc.AuthenticationToken authentication token} after a successful ID Site login.
     *
     * @see com.stormpath.shiro.authc.IdSiteAccountIDField
     * @since 0.7.0
     */
    public void setIdSitePrincipalAccountIdField(String idField) {
        Assert.notNull(idField);
        this.idSitePrincipalAccountIdField = IdSiteAccountIDField.fromName(idField);
    }

    /**
     * Returns the account field that will be used as the principal for the {@link org.apache.shiro.authc.AuthenticationToken authentication token}
     * after a successful ID Site login.
     *
     * @return the account field that will be used as the principal for the {@link org.apache.shiro.authc.AuthenticationToken authentication token}
     * after a successful ID Site login.
     *
     * @since 0.7.0
     */
    public String getIdSitePrincipalAccountIdField() {
        return this.idSitePrincipalAccountIdField.toString();
    }

    //@since 0.4.0
    private String getIdSitePrincipalValue(Account account) {
        switch (this.idSitePrincipalAccountIdField) {
            case EMAIL:
                return account.getEmail();
            case USERNAME:
                return account.getUsername();
            default:
                throw new UnsupportedOperationException("unrecognized idSitePrincipalAccountIdField value: " + this.idSitePrincipalAccountIdField);
        }
    }


}
