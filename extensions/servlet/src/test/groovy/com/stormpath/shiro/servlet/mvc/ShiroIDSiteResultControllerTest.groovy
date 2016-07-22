package com.stormpath.shiro.servlet.mvc

import com.stormpath.sdk.account.Account
import com.stormpath.sdk.idsite.AuthenticationResult
import com.stormpath.sdk.servlet.event.RequestEvent
import com.stormpath.sdk.servlet.event.impl.Publisher
import com.stormpath.sdk.servlet.http.Saver
import com.stormpath.shiro.realm.StormpathWebRealm
import com.stormpath.shiro.servlet.ShiroTestSupport
import org.apache.shiro.authc.AuthenticationException
import org.apache.shiro.config.ConfigurationException
import org.apache.shiro.subject.Subject
import org.apache.shiro.util.ThreadContext
import org.testng.annotations.Test

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import static org.easymock.EasyMock.*
import static org.testng.Assert.fail

/**
 * Tests for {@link ShiroIDSiteResultController}.
 */
class ShiroIDSiteResultControllerTest extends ShiroTestSupport {

    @Test
    void testOnAuthenticationSuccess() {

        def request = createMock(HttpServletRequest)
        def response = createMock(HttpServletResponse)
        def authResult = createMock(AuthenticationResult)
        def account = createMock(Account)
        def subject = createMock(Subject)
        Publisher<RequestEvent> publisher = createMock(Publisher)
        Saver<com.stormpath.sdk.authc.AuthenticationResult> saver = createMock(Saver)

        expect(authResult.getAccount()).andReturn(account).times(2)
        saver.set(eq(request), eq(response), anyObject())
        publisher.publish(anyObject())
        subject.login(anyObject(StormpathWebRealm.AccessTokenResultAuthToken))

        replay request, response, authResult, account, saver, publisher, subject

        ThreadContext.bind(subject)

        def shiroIDSiteResultController = new ShiroIDSiteResultController()
        shiroIDSiteResultController.authenticationResultSaver = saver
        shiroIDSiteResultController.eventPublisher = publisher
        shiroIDSiteResultController.onAuthentication(request, response, authResult)

        verify request, response, authResult, account, saver, publisher, subject
    }

    @Test
    void testOnAuthenticationFailure() {

        def request = createMock(HttpServletRequest)
        def response = createMock(HttpServletResponse)
        def authResult = createMock(AuthenticationResult)
        def account = createMock(Account)
        def subject = createMock(Subject)
        Publisher<RequestEvent> publisher = createMock(Publisher)
        Saver<com.stormpath.sdk.authc.AuthenticationResult> saver = createMock(Saver)

        expect(authResult.getAccount()).andReturn(account).times(2)
        saver.set(eq(request), eq(response), anyObject())
        publisher.publish(anyObject())
        subject.login(anyObject(StormpathWebRealm.AccessTokenResultAuthToken))
        expectLastCall().andThrow(new AuthenticationException("Expected test exception"))

        replay request, response, authResult, account, saver, publisher, subject

        ThreadContext.bind(subject)

        def shiroIDSiteResultController = new ShiroIDSiteResultController()
        shiroIDSiteResultController.authenticationResultSaver = saver
        shiroIDSiteResultController.eventPublisher = publisher

        try {
            shiroIDSiteResultController.onAuthentication(request, response, authResult)
            fail("Expected ConfigurationException")
        }
        catch (ConfigurationException e) {
            // expected
        }

        verify request, response, authResult, account, saver, publisher, subject
    }
}
