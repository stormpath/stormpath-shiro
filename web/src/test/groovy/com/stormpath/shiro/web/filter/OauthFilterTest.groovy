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
package com.stormpath.shiro.web.filter

import com.stormpath.shiro.web.service.ProviderService
import org.apache.shiro.authc.AuthenticationException
import org.apache.shiro.authc.AuthenticationToken
import org.apache.shiro.session.Session
import org.apache.shiro.subject.Subject
import org.apache.shiro.subject.SubjectContext
import org.apache.shiro.util.ThreadContext
import org.junit.Test

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import static org.easymock.EasyMock.replay
import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

/**
 * @since 0.7.0
 */
class OauthFilterTest {

    @Test
    public void testOnAccessDeniedLoginOK() {

        def request = createStrictMock(HttpServletRequest)
        def response = createStrictMock(HttpServletResponse)
        def service = createStrictMock(ProviderService)
        def securityManager = createStrictMock(org.apache.shiro.mgt.SecurityManager)
        def oauthFilter = createMockBuilder(OauthFilter.class)
                .withConstructor(service)
                .addMockedMethod("getOauthAuthenticatingToken", String)
                .createMock();
        def authenticationToken = createStrictMock(AuthenticationToken)
        def subject = createNiceMock(Subject)

        expect(request.getParameter("code")).andReturn("aProviderCode")
        expect(oauthFilter.getOauthAuthenticatingToken("aProviderCode")).andReturn(authenticationToken)
        expect(request.getContextPath()).andReturn("/oauthCallback")
        expect(response.encodeRedirectURL("/oauthCallback/")).andReturn("/oauthCallback/")
        expect(response.sendRedirect("/oauthCallback/"))
        expect(securityManager.createSubject(anyObject(SubjectContext))).andReturn(subject)

        replay request, response, securityManager, service, oauthFilter, authenticationToken

        ThreadContext.bind(securityManager)

        assertFalse(oauthFilter.onAccessDenied(request, response))

        verify request, response, securityManager, service, oauthFilter, authenticationToken

    }

    @Test
    public void testOnAccessDeniedLoginFailWithAccountStoresOK() {

        def request = createStrictMock(HttpServletRequest)
        def response = createStrictMock(HttpServletResponse)
        def service = createStrictMock(ProviderService)
        def securityManager = createStrictMock(org.apache.shiro.mgt.SecurityManager)
        def subject = createStrictMock(Subject)
        def oauthFilter = createMockBuilder(OauthFilter.class)
                .withConstructor(service)
                .addMockedMethod("getOauthAuthenticatingToken", String)
                .createMock();
        def authenticationToken = createStrictMock(AuthenticationToken)

        expect(request.getParameter("code")).andReturn("aProviderCode")
        expect(oauthFilter.getOauthAuthenticatingToken("aProviderCode")).andReturn(authenticationToken)
        expect(subject.login(authenticationToken)).andThrow(new AuthenticationException())
        expect(request.getContextPath()).andReturn("/oauthCallback")
        expect(response.encodeRedirectURL("/oauthCallback/login.jsp?source=nullLoginError")).andReturn("/oauthCallback/login.jsp?source=nullLoginError")
        expect(response.sendRedirect("/oauthCallback/login.jsp?source=nullLoginError"))
        expect(service.hasProviderBasedAccountStore()).andReturn(true)

        replay request, response, securityManager, subject, service, oauthFilter, authenticationToken

        ThreadContext.bind(securityManager)
        ThreadContext.bind(subject)

        assertTrue(oauthFilter.onAccessDenied(request, response))

        verify request, response, securityManager, subject, service, oauthFilter, authenticationToken

    }

    @Test
    public void testOnAccessDeniedTryToCreateDirectory() {

        def request = createStrictMock(HttpServletRequest)
        def response = createStrictMock(HttpServletResponse)
        def service = createStrictMock(ProviderService)
        def securityManager = createStrictMock(org.apache.shiro.mgt.SecurityManager)
        def subject = createStrictMock(Subject)
        def oauthFilter = createMockBuilder(OauthFilter.class)
                .withConstructor(service)
                .addMockedMethod("getOauthAuthenticatingToken", String)
                .createMock();
        def authenticationToken = createStrictMock(AuthenticationToken)
        def session = createStrictMock(Session)

        expect(request.getParameter("code")).andReturn("aProviderCode")
        expect(oauthFilter.getOauthAuthenticatingToken("aProviderCode")).andReturn(authenticationToken)
        expect(subject.login(authenticationToken)).andThrow(new AuthenticationException())
        expect(request.getContextPath()).andReturn("/oauthCallback")
        expect(service.hasProviderBasedAccountStore()).andReturn(false)
        expect(service.createProviderAccountStore())
        expect(oauthFilter.getOauthAuthenticatingToken("aProviderCode")).andReturn(authenticationToken)
        expect(subject.login(authenticationToken))
        expect(subject.getSession(false)).andReturn(session)
        expect(session.getAttribute("shiroSavedRequest")).andReturn(null)
        expect(response.encodeRedirectURL("/oauthCallback/")).andReturn("/oauthCallback/")
        expect(response.sendRedirect("/oauthCallback/"))

        replay request, response, securityManager, subject, service, oauthFilter, authenticationToken, session

        ThreadContext.bind(securityManager)
        ThreadContext.bind(subject)

        assertFalse(oauthFilter.onAccessDenied(request, response))

        verify request, response, securityManager, subject, service, oauthFilter, authenticationToken, session

    }
}
