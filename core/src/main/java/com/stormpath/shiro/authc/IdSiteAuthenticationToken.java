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

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.lang.Assert;
import org.apache.shiro.authc.AuthenticationToken;

/**
 * This {@link AuthenticationToken} sub-class is specific to ID Site. It is used to represent an already logged in user via ID Site.
 * <p/>
 * Here we hold the {@link com.stormpath.sdk.account.Account authenticated Stormpath account} tha was obtained after the login.
 * <p/>
 * At the time this token is created the user has already being successfully authenticated in the ID Site. This token must be passed through
 * Shiro's login process so the framework is aware of the recently logged in user. For example:
 * <pre>
 *     Account account = callbackHandler.getAccountResult().getAccount();
 *     SecurityUtils.getSubject().login(new IdSiteAuthenticationToken(account.getEmail(), account));
 * </pre>
 * <p/>
 * After that line is executed, Shiro will be fully aware of the user's information: the Stormpath Account, its roles and permissions.
 *
 * @since 0.7.0
 */
public class IdSiteAuthenticationToken implements AuthenticationToken {

    private final String principal;
    private final Account account;

    /**
     * Since this {@link org.apache.shiro.authc.AuthenticationToken token} represents and already logged in account via ID Site,
     * the principal can be anything that univocally identifies the logged in user: for example, username or email.
     *
     * @param principal univocally identification of the logged in user: for example, the account's username or email.
     * @param account the {@link com.stormpath.sdk.account.Account Account} received from the {@link com.stormpath.sdk.idsite.IdSiteCallbackHandler
     * IdSiteCallbackHandler} after a successful login.
     */
    public IdSiteAuthenticationToken(String principal, Account account) {
        Assert.notNull(principal);
        Assert.notNull(account);
        this.principal = principal;
        this.account = account;
    }

    /**
     * Either the {@link com.stormpath.sdk.account.Account Account}'s email or username used when this token was created.
     *
     * @return either the {@link com.stormpath.sdk.account.Account Account}'s email or username.
     */
    @Override
    public Object getPrincipal() {
        return this.principal;
    }

    /**
     * Always returns `null` as this ID Site token does not actually require any credentials
     *
     * @return `null` as this ID Site token does not actually require any credentials.
     *
     */
    @Override
    public Object getCredentials() {
        return null;
    }

    /**
     * Since this {@link org.apache.shiro.authc.AuthenticationToken token} represents and already logged in account via ID Site,
     * the {@link com.stormpath.sdk.account.Account Stormpath account} is actually already available when this token is created.
     * We keep it here so the {@link com.stormpath.shiro.realm.ApplicationRealm ApplicationRealm} does not need to retrieve it again
     * from Stormpath.
     *
     * @return the {@link Account authenticated Stormpath account} tha was obtained after a successful ID Site login.
     */
    public Account getAccount() {
        return this.account;
    }

}
