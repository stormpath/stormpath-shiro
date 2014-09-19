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
package com.stormpath.shiro.servlet.listener;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.idsite.*;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.shiro.authc.IdSiteAccountIDField;
import com.stormpath.shiro.authc.IdSiteAuthenticationToken;
import org.apache.shiro.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This listener will be notified about successful ID Site activities such as registration, authentication and logout.
 * <p/>
 * In case this listener wants to be overriden, it must be assigned to the {@link com.stormpath.shiro.servlet.http.IdSiteServlet
 * IdSiteServlet} in `shiro.ini`. For example:
 * <p/>
 * <pre>
 *      idSiteResultListener = com.mycompany.myshiroapp.idsite.IdSiteListener
 *      idSiteServlet = com.stormpath.shiro.servlet.http.IdSiteServlet
 *      idSiteServlet.idSiteResultListener = $idSiteResultListener
 * </pre>
 *
 * @since 0.7.0
 */
public class IdSiteListener implements IdSiteResultListener {
    private static final Logger logger = LoggerFactory.getLogger(IdSiteListener.class);

    private IdSiteAccountIDField idSitePrincipalAccountIdField = IdSiteAccountIDField.EMAIL;

    @Override
    public void onRegistered(RegistrationResult result) {
        logger.debug("Successful Id Site registration for account: " + result.getAccount().getEmail());
    }

    @Override
    public void onAuthenticated(AuthenticationResult result) {
        Account account = result.getAccount();
        SecurityUtils.getSubject().login(new IdSiteAuthenticationToken(getIdSitePrincipalValue(account), account));
        logger.debug("Successful Id Site authentication for account: " + account.getEmail());
    }

    @Override
    public void onLogout(LogoutResult result) {
        SecurityUtils.getSubject().logout();
        logger.debug("Successful Id Site logout for account: " + result.getAccount().getEmail());
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

    /**
     *  @since 0.7.0
     */
    private String getIdSitePrincipalValue(Account account) {
        switch (this.idSitePrincipalAccountIdField) {
            case EMAIL:
                return account.getEmail();
            case USERNAME:
                return account.getUsername();
            default:
                throw new UnsupportedOperationException("Unrecognized idSitePrincipalAccountIdField value: " + this.idSitePrincipalAccountIdField);
        }
    }

}
