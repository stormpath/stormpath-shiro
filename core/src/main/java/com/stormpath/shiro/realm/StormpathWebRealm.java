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
import org.apache.shiro.authc.*;
import org.apache.shiro.authc.credential.AllowAllCredentialsMatcher;
import org.apache.shiro.subject.PrincipalCollection;

/**
 * Realm implantation that pushes a stormpath-servlet authenticated/authorized Account into a Shiro Subject.
 */
public class StormpathWebRealm extends ApplicationRealm {

    public StormpathWebRealm() {
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
            principals = createPrincipals(accessAuthToken.getAccount());
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
