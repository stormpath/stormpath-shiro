package com.stormpath.shiro.servlet.mvc

import com.stormpath.sdk.account.Account
import com.stormpath.shiro.realm.StormpathWebRealm
import com.stormpath.shiro.servlet.ShiroTestSupport
import org.apache.shiro.SecurityUtils
import org.apache.shiro.authc.AuthenticationException
import org.apache.shiro.authc.AuthenticationToken
import org.apache.shiro.config.ConfigurationException
import org.apache.shiro.mgt.SecurityManager
import org.apache.shiro.subject.Subject
import org.apache.shiro.subject.SubjectContext
import org.easymock.Capture
import org.testng.annotations.Test

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import static org.easymock.EasyMock.*
import static org.testng.Assert.*

/**
 * Tests for {@link ShiroLoginHandler}.
 */
@Test(singleThreaded = true)
public class ShiroLoginHandlerTest extends ShiroTestSupport {

    @Test
    public void testLoginSuccess() {

        def account = createMock(Account)
        def securityManager = createMock(SecurityManager)
        def subject = createMock(Subject)

        def request = createMock(HttpServletRequest)
        def response = createMock(HttpServletResponse)

        Capture<StormpathWebRealm.AccessTokenResultAuthToken> loginCapture = new Capture<>()

        expect(securityManager.createSubject(anyObject(SubjectContext))).andReturn(subject)
        subject.login(capture(loginCapture))

        replay request, response, securityManager, subject, account

        SecurityUtils.setSecurityManager(securityManager)

        ShiroLoginHandler loginHandler = new ShiroLoginHandler()
        loginHandler.handle(request, response, account)

        verify request, response, securityManager, subject, account

        def actualToken = loginCapture.value
        assertSame account, actualToken.account

    }

    @Test
    public void testLoginFailure() {

        def account = createMock(Account)
        def securityManager = createMock(SecurityManager)
        def subject = createMock(Subject)

        def request = createMock(HttpServletRequest)
        def response = createMock(HttpServletResponse)

        expect(securityManager.createSubject(anyObject(SubjectContext))).andReturn(subject)
        expect(subject.login(anyObject(AuthenticationToken))).andThrow(new AuthenticationException("Expected test exception"))

        replay request, response, securityManager, subject, account

        SecurityUtils.setSecurityManager(securityManager)

        ShiroLoginHandler loginHandler = new ShiroLoginHandler()

        try {
            loginHandler.handle(request, response, account)
            fail("expected ConfigurationException")
        }
        catch(ConfigurationException e) {
            // expected
        }
        verify request, response, securityManager, subject, account
    }
}
