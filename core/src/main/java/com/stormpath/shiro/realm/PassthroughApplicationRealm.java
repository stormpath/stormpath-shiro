/*
 * Copyright 2012 Stormpath, Inc.
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
package com.stormpath.shiro.realm;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.account.AccountStatus;
import org.apache.shiro.authc.*;
import org.apache.shiro.authc.credential.AllowAllCredentialsMatcher;
import org.apache.shiro.subject.PrincipalCollection;

/**
 * An {@link ApplicationRealm} implementation that accepts an already authenticated {@link Account}. This realm is
 * typically used in conjunction one of Stormpath's web integrations, were the user authentication happens through the
 * Stormpath API via a servlet filter <code>StormpathFilter</code>. This realm has not direct dependencies to any web
 * framework, and could be used in other integrations with Stormpath's java SDK. <BR/><BR/>
 *
 * When integration with Stormpath using a username and password, use {@link ApplicationRealm}.<BR/><BR/>
 *
 * Configuration information for this realm is also the same as {@link ApplicationRealm}.
 *
 * @see ApplicationRealm
 * @since 0.7.0
 */
public class PassthroughApplicationRealm extends ApplicationRealm {

    public PassthroughApplicationRealm() {
        super();
        this.setCredentialsMatcher(new AllowAllCredentialsMatcher());
        this.setAuthenticationTokenClass(AccountAuthenticationToken.class);
        this.setCachingEnabled(false);
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {

        AccountAuthenticationToken accessAuthToken = (AccountAuthenticationToken) token;

        PrincipalCollection principals;

        try {

            Account account = accessAuthToken.getAccount();
            // we should not reach this point if the account is not enabled, but, just in case.
            if (AccountStatus.ENABLED != account.getStatus()) {
                throw new AuthenticationException("Account for user [" + account.getHref() + "] is not enabled.");
            }

            principals = createPrincipals(account);
        } catch (Exception e) {
            throw new AuthenticationException("Unable to obtain authenticated account properties.", e);
        }

        return new SimpleAuthenticationInfo(principals, null);
    }


    public static class AccountAuthenticationToken implements AuthenticationToken {

        final private Account account;

        public AccountAuthenticationToken(Account account) {
            this.account = account;
        }

        public Account getAccount() {
            return account;
        }

        @Override
        public Object getPrincipal() {
            return getAccount();
        }

        @Override
        public Object getCredentials() {
            return null;
        }
    }
}
