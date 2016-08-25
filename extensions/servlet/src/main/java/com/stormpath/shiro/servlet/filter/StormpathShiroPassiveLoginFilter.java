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
package com.stormpath.shiro.servlet.filter;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.servlet.account.AccountResolver;
import com.stormpath.shiro.realm.PassthroughApplicationRealm.AccountAuthenticationToken;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.servlet.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;

/**
 * If a Stormpath Account is found via the AccountResolver, and the current subject is NOT already logged in,
 * A login request will be made with a {@link AccountAuthenticationToken}.
 */
public class StormpathShiroPassiveLoginFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(ServletRequest request, ServletResponse response, FilterChain chain) throws ServletException, IOException {

        // if we have a subject and an account, then perform the shiro login

        Subject subject = SecurityUtils.getSubject();
        if (!subject.isAuthenticated()) {
            Account account = AccountResolver.INSTANCE.getAccount(request);
            if (account != null) {
                subject.login(new AccountAuthenticationToken(account));
            }
        }

        chain.doFilter(request, response);
    }
}
