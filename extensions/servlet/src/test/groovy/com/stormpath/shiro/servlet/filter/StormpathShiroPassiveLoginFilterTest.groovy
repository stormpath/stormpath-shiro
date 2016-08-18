package com.stormpath.shiro.servlet.filter

import com.stormpath.sdk.account.Account
import com.stormpath.shiro.realm.StormpathWebRealm.AccountAuthenticationToken
import com.stormpath.shiro.servlet.ShiroTestSupport
import org.apache.shiro.subject.Subject
import org.apache.shiro.util.ThreadContext
import org.easymock.Capture
import org.easymock.EasyMock
import org.testng.annotations.Test

import javax.servlet.FilterChain
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import static org.easymock.EasyMock.*
import static org.hamcrest.Matchers.*
import static org.hamcrest.MatcherAssert.*

/**
 * Tests for {@link StormpathShiroPassiveLoginFilter}.
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
