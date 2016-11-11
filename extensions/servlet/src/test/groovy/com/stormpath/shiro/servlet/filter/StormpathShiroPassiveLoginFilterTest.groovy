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
package com.stormpath.shiro.servlet.filter

import com.stormpath.sdk.account.Account
import com.stormpath.shiro.realm.PassthroughApplicationRealm.AccountAuthenticationToken
import com.stormpath.shiro.servlet.ShiroTestSupport
import org.apache.shiro.subject.Subject
import org.apache.shiro.util.ThreadContext
import org.easymock.Capture
import org.testng.annotations.Test

import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import static org.easymock.EasyMock.*

/**
 * Tests for {@link StormpathShiroPassiveLoginFilter}.
 * @since 0.7.0
 */
class StormpathShiroPassiveLoginFilterTest extends ShiroTestSupport {

    @Test
    public void testSubjectAlreadyLoggedIn() {

        def request = createMock(HttpServletRequest)
        def response = createMock(HttpServletResponse)
        def chain = createMock(FilterChain)
        def subject = createMock(Subject)

        expect(subject.isAuthenticated()).andReturn(true)
        chain.doFilter(request, response)
        replay request, response, chain, subject

        ThreadContext.bind(subject)
        new StormpathShiroPassiveLoginFilter().doFilterInternal(request, response, chain)

        verify request, response, chain, subject
    }

    @Test
    public void testSubjectNotLoggedInNoAccount() {

        def request = createMock(HttpServletRequest)
        def response = createMock(HttpServletResponse)
        def chain = createMock(FilterChain)
        def subject = createMock(Subject)

        expect(subject.isAuthenticated()).andReturn(false)
        expect(request.getAttribute(Account.getName())).andReturn(null)
        expect(request.getSession(false)).andReturn(null)
        chain.doFilter(request, response)
        replay request, response, chain, subject

        ThreadContext.bind(subject)
        new StormpathShiroPassiveLoginFilter().doFilterInternal(request, response, chain)

        verify request, response, chain, subject
    }

    @Test
    public void testSubjectNotLoggedValidAccountInRequest() {

        def request = createMock(HttpServletRequest)
        def response = createMock(HttpServletResponse)
        def chain = createMock(FilterChain)
        def subject = createMock(Subject)
        def account = createMock(Account)
        def tokenCapture = new Capture<AccountAuthenticationToken>()

        expect(subject.isAuthenticated()).andReturn(false)
        expect(request.getAttribute(Account.getName())).andReturn(account)
        subject.login(capture(tokenCapture))
        chain.doFilter(request, response)
        replay request, response, chain, subject, account

        ThreadContext.bind(subject)
        new StormpathShiroPassiveLoginFilter().doFilterInternal(request, response, chain)

        verify request, response, chain, subject, account
    }
}
