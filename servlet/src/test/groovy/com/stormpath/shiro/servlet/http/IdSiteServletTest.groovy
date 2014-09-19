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
package com.stormpath.shiro.servlet.http

import com.stormpath.sdk.account.Account
import com.stormpath.sdk.application.Application
import com.stormpath.sdk.client.Client
import com.stormpath.sdk.idsite.AccountResult
import com.stormpath.sdk.idsite.AuthenticationResult
import com.stormpath.sdk.idsite.IdSiteCallbackHandler
import com.stormpath.sdk.idsite.IdSiteUrlBuilder
import com.stormpath.sdk.idsite.LogoutResult
import com.stormpath.shiro.realm.ApplicationRealm
import com.stormpath.shiro.authc.IdSiteAuthenticationToken
import com.stormpath.shiro.servlet.conf.Configuration
import com.stormpath.shiro.servlet.conf.UrlFor
import com.stormpath.shiro.servlet.listener.IdSiteListener
import com.stormpath.shiro.servlet.service.AbstractService
import com.stormpath.shiro.servlet.service.IdSiteService
import org.apache.shiro.mgt.DefaultSecurityManager
import org.apache.shiro.mgt.SubjectFactory
import org.apache.shiro.subject.Subject
import org.apache.shiro.subject.support.DefaultSubjectContext
import org.apache.shiro.util.ThreadContext
import org.easymock.IAnswer
import org.junit.After
import org.junit.Test

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import java.lang.reflect.Field

import static org.easymock.EasyMock.*
import static org.junit.Assert.*

class IdSiteServletTest {

    @After
    public void tearDown() {
        //Since idSiteService is a singleton, let's set applicationRealm and application to null so other tests can start with a clean instance
        setNewValue(AbstractService, IdSiteService.getInstance(), "applicationRealm", null)
        setNewValue(AbstractService, IdSiteService.getInstance(), "application", null)

        //Let's clean the ThreadContext
        ThreadContext.unbindSecurityManager()
        ThreadContext.unbindSubject()
    }

    @Test
    public void testLogin() {

        def client = createStrictMock(Client)
        def application = createStrictMock(Application)
        def idSiteUrlBuilder = createStrictMock(IdSiteUrlBuilder)
        def request = createStrictMock(HttpServletRequest)
        def response = createStrictMock(HttpServletResponse)

        def appHref = 'https://api.stormpath.com/v1/applications/foo'
        def callbackUri = "http://api.stormpath.com/sso?jwtRequest=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpUXQiOoE1MDk4ODIzNTIsImp0aSI6IjUzODU2YmJmLTBlOTQtNDFmNC05OTJmLWZiYjFmZjA5MWVkZCIsImlzcyI6IjZKUVBDSVRNTzVFOEhFS042REtCVDdSNTIiLCJzdWIiOiJodHRwczovL2FwaS5zdG9ybXBhdGguY29tL3YxL2FwcGxpY2F0aW6ucy8zVHFieVoxcW83NGVETTRnVG7ySDk0IiwiY2JfdXJpIjoiaHR9cDovL2xvY2FsaG9zdDo4MDgwL2lkc2l0ZS9jYWxsYmFja0xvZ2luIn0.hBva8p4Wy9hAu5nR9euJcMRI0qR0Xkvna-GlBnMOGSQ"

        expect(client.getResource(eq(appHref), same(Application))).andReturn(application)
        expect(application.newIdSiteUrlBuilder()).andReturn(idSiteUrlBuilder)
        expect(idSiteUrlBuilder.setCallbackUri(Configuration.getBaseUrl() + UrlFor.get("idsite_login_callback.action"))).andReturn(idSiteUrlBuilder)
        expect(idSiteUrlBuilder.build()).andReturn(callbackUri)

        expect(request.getRequestURI()).andReturn("/idsite/login")
        expect(response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, post-check=0, pre-check=0"))
        expect(response.setHeader("Pragma", "no-cache"))
        expect(response.sendRedirect(callbackUri))

        replay client, application, idSiteUrlBuilder, request, response

        //setup a quick Shiro SecurityManager using the ApplicationRealm
        def realm = new ApplicationRealm(client: client, applicationRestUrl: appHref)
        ThreadContext.bind(new DefaultSecurityManager(realm))

        IdSiteServlet servlet = new IdSiteServlet()
        servlet.doGet(request, response)

        verify client, application, idSiteUrlBuilder, request, response
    }

    @Test
    public void testLoginCallback() {

        def client = createStrictMock(Client)
        def application = createStrictMock(Application)

        def request = createStrictMock(HttpServletRequest)
        def response = createStrictMock(HttpServletResponse)
        def applicationRealm = createStrictMock(ApplicationRealm)
        def idSiteListener = new IdSiteListener()

        def subjectFactory = createStrictMock(SubjectFactory)
        def subject = createStrictMock(Subject)

        def callbackHandler = createStrictMock(IdSiteCallbackHandler)
        def accountResult = createStrictMock(AccountResult)
        def account = createStrictMock(Account)
        def authenticationResult = createStrictMock(AuthenticationResult)

        def accountEmail = "some@email.com"
        def appHref = 'https://api.stormpath.com/v1/applications/foo'

        expect(applicationRealm.getClient()).andReturn(client)
        expect(applicationRealm.getApplicationRestUrl()).andReturn(appHref)
        expect(client.getResource(eq(appHref), same(Application))).andReturn(application)
        expect(application.newIdSiteCallbackHandler(request)).andAnswer( new IAnswer<IdSiteCallbackHandler>() {
            IdSiteCallbackHandler answer() throws Throwable {
                def thisRequest = getCurrentArguments()[0] as HttpServletRequest

                assertEquals thisRequest, request

                return callbackHandler
            }
        })
        expect(request.getRequestURI()).andReturn("/idsite/callbackLogin")
        expect(callbackHandler.setResultListener(idSiteListener)).andReturn(callbackHandler)
        expect(callbackHandler.getAccountResult()).andAnswer( new IAnswer<AccountResult>() {
            AccountResult answer() throws Throwable {
                idSiteListener.onAuthenticated(authenticationResult)
                return accountResult
            }
        })
        expect(authenticationResult.getAccount()).andReturn(account)
        expect(subjectFactory.createSubject(anyObject(DefaultSubjectContext))).andReturn(subject)
        expect(subject.getSession(false)).andReturn(null)
        expect(subject.isRunAs()).andReturn(false)
        expect(subject.getPrincipals()).andReturn(null)
        expect(subject.getSession(false)).andReturn(null) times 2
        expect(subject.isAuthenticated()).andReturn(false)
        expect(subject.login(anyObject(IdSiteAuthenticationToken))).andAnswer( new IAnswer<Void>() {
            Void answer() throws Throwable {
                def idSiteAuthenticationToken = getCurrentArguments()[0] as IdSiteAuthenticationToken
                assertEquals idSiteAuthenticationToken.getPrincipal(), accountEmail
                assertEquals idSiteAuthenticationToken.getAccount(), account
                assertEquals idSiteAuthenticationToken.getCredentials(), null
            }
        })

        expect(account.getEmail()).andReturn(accountEmail) times 2

        expect(response.sendRedirect("http://localhost:8080/index.jsp"))

        replay client, application, request, response, subjectFactory, subject, applicationRealm,
                callbackHandler, accountResult, account, authenticationResult

        //setup a quick Shiro SecurityManager using the ApplicationRealm
        def defaultSecurityManager = new DefaultSecurityManager(applicationRealm)
        defaultSecurityManager.subjectFactory = subjectFactory
        ThreadContext.bind(defaultSecurityManager)

        IdSiteServlet servlet = new IdSiteServlet()
        servlet.idSiteResultListener = idSiteListener
        servlet.doGet(request, response)

        verify client, application, request, response, subjectFactory, subject, applicationRealm,
                callbackHandler, accountResult, account, authenticationResult
    }

    @Test
    public void testLogout() {

        def client = createStrictMock(Client)
        def application = createStrictMock(Application)
        def idSiteUrlBuilder = createStrictMock(IdSiteUrlBuilder)
        def request = createStrictMock(HttpServletRequest)
        def response = createStrictMock(HttpServletResponse)

        def appHref = 'https://api.stormpath.com/v1/applications/foo'
        def callbackUri = "http://api.stormpath.com/sso?jwtRequest=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpUXQiOoE1MDk4ODIzNTIsImp0aSI6IjUzODU2YmJmLTBlOTQtNDFmNC05OTJmLWZiYjFmZjA5MWVkZCIsImlzcyI6IjZKUVBDSVRNTzVFOEhFS042REtCVDdSNTIiLCJzdWIiOiJodHRwczovL2FwaS5zdG9ybXBhdGguY29tL3YxL2FwcGxpY2F0aW6ucy8zVHFieVoxcW83NGVETTRnVG7ySDk0IiwiY2JfdXJpIjoiaHR9cDovL2xvY2FsaG9zdDo4MDgwL2lkc2l0ZS9jYWxsYmFja0xvZ2luIn0.hBva8p4Wy9hAu5nR9euJcMRI0qR0Xkvna-GlBnMOGSQ"

        expect(client.getResource(eq(appHref), same(Application))).andReturn(application)
        expect(application.newIdSiteUrlBuilder()).andReturn(idSiteUrlBuilder)
        expect(idSiteUrlBuilder.setCallbackUri("http://localhost:8080/idsite/callbackLogout")).andReturn(idSiteUrlBuilder)
        expect(idSiteUrlBuilder.forLogout()).andReturn(idSiteUrlBuilder)
        expect(idSiteUrlBuilder.build()).andReturn(callbackUri)

        expect(request.getRequestURI()).andReturn("/idsite/logout")
        expect(response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, post-check=0, pre-check=0"))
        expect(response.setHeader("Pragma", "no-cache"))
        expect(response.sendRedirect(callbackUri))

        replay client, application, idSiteUrlBuilder, request, response

        //setup a quick Shiro SecurityManager using the ApplicationRealm
        def realm = new ApplicationRealm(client: client, applicationRestUrl: appHref)
        ThreadContext.bind(new DefaultSecurityManager(realm))

        IdSiteServlet servlet = new IdSiteServlet()
        servlet.doGet(request, response)

        verify client, application, idSiteUrlBuilder, request, response
    }

    @Test
    public void testLogoutCallback() {

        def client = createStrictMock(Client)
        def application = createStrictMock(Application)

        def request = createStrictMock(HttpServletRequest)
        def response = createStrictMock(HttpServletResponse)
        def applicationRealm = createStrictMock(ApplicationRealm)
        def idSiteListener = new IdSiteListener()

        def subjectFactory = createStrictMock(SubjectFactory)
        def subject = createStrictMock(Subject)

        def callbackUri = Configuration.getLogoutRedirectUri()
        def callbackHandler = createStrictMock(IdSiteCallbackHandler)
        def accountResult = createStrictMock(AccountResult)
        def account = createStrictMock(Account)
        def logoutResult = createStrictMock(LogoutResult)

        def accountEmail = "some@email.com"
        def appHref = 'https://api.stormpath.com/v1/applications/foo'

        expect(applicationRealm.getClient()).andReturn(client)

        expect(applicationRealm.getApplicationRestUrl()).andReturn(appHref)
        expect(client.getResource(eq(appHref), same(Application))).andReturn(application)
        expect(application.newIdSiteCallbackHandler(request)).andAnswer( new IAnswer<IdSiteCallbackHandler>() {
            IdSiteCallbackHandler answer() throws Throwable {
                def thisRequest = getCurrentArguments()[0] as HttpServletRequest

                assertEquals thisRequest, request

                return callbackHandler
            }
        })
        expect(request.getRequestURI()).andReturn("/idsite/callbackLogout")
        expect(callbackHandler.setResultListener(idSiteListener)).andReturn(callbackHandler)
        expect(callbackHandler.getAccountResult()).andAnswer( new IAnswer<AccountResult>() {
            AccountResult answer() throws Throwable {
                idSiteListener.onLogout(logoutResult)
                return accountResult
            }
        })
        expect(subjectFactory.createSubject(anyObject(DefaultSubjectContext))).andReturn(subject)
        expect(subject.getSession(false)).andReturn(null)
        expect(subject.isRunAs()).andReturn(false)
        expect(subject.getPrincipals()).andReturn(null)
        expect(subject.getSession(false)).andReturn(null) times 2
        expect(subject.isAuthenticated()).andReturn(false)
        expect(subject.logout())

        expect(logoutResult.getAccount()).andReturn(account)
        expect(account.getEmail()).andReturn(accountEmail)

        expect(response.sendRedirect(callbackUri))

        replay client, application, request, response, subjectFactory, subject, applicationRealm,
                callbackHandler, accountResult, account, logoutResult

        //setup a quick Shiro SecurityManager using the ApplicationRealm
        def defaultSecurityManager = new DefaultSecurityManager(applicationRealm)
        defaultSecurityManager.subjectFactory = subjectFactory
        ThreadContext.bind(defaultSecurityManager)

        IdSiteServlet servlet = new IdSiteServlet()
        servlet.idSiteResultListener = idSiteListener
        servlet.doGet(request, response)

        verify client, application, request, response, subjectFactory, subject, applicationRealm,
                callbackHandler, accountResult, account, logoutResult
    }


    private void setNewValue(Class clazz, Object object, String fieldName, Object value){
        Field field = clazz.getDeclaredField(fieldName)
        field.setAccessible(true)
        field.set(object, value)
    }

}

