package com.stormpath.shiro.servlet.mvc

import com.stormpath.sdk.servlet.filter.ContentNegotiationResolver
import com.stormpath.sdk.servlet.http.MediaType
import com.stormpath.sdk.servlet.http.impl.DefaultUserAgent
import com.stormpath.shiro.servlet.ShiroTestSupport
import org.apache.shiro.session.SessionException
import org.apache.shiro.subject.Subject
import org.apache.shiro.util.ThreadContext
import org.testng.annotations.Test

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import static org.easymock.EasyMock.*

/**
 * Test for {@link ShiroLogoutController}.
 */
public class ShiroLogoutControllerTest extends ShiroTestSupport {

    @Test
    public void testLogout() {

        def request = createMock(HttpServletRequest)
        def response = createMock(HttpServletResponse)
        def subject = createMock(Subject)
        def userAgent = createMock(DefaultUserAgent)
        def contentNegotiationResolver = createMock(ContentNegotiationResolver)
        subject.logout()
        request.logout()
        expect(request.getSession(false)).andReturn(null)
        expect(contentNegotiationResolver.getContentType(anyObject(HttpServletRequest), anyObject(HttpServletResponse), anyObject())).andReturn(MediaType.APPLICATION_JSON)
        response.setStatus(200)

        replay request, response, subject, userAgent, contentNegotiationResolver

        // bind a subject, this will be the subject that will be logged out.
        ThreadContext.bind(subject)

        def controller = new ShiroLogoutController()
        controller.setContentNegotiationResolver(contentNegotiationResolver)
        controller.doPost(request, response)

        verify request, response, subject, userAgent, contentNegotiationResolver
    }

    /**
     * If an exception is thrown when a Shiro logout is called it should be ignored.
     */
    @Test
    public void testLogoutFailure() {

        def request = createMock(HttpServletRequest)
        def response = createMock(HttpServletResponse)
        def subject = createMock(Subject)
        def userAgent = createMock(DefaultUserAgent)
        def contentNegotiationResolver = createMock(ContentNegotiationResolver)
        subject.logout()
        expectLastCall().andThrow(new SessionException("expected test failure"))
        request.logout()
        expect(request.getSession(false)).andReturn(null)
        expect(contentNegotiationResolver.getContentType(anyObject(HttpServletRequest), anyObject(HttpServletResponse), anyObject())).andReturn(MediaType.APPLICATION_JSON)
        response.setStatus(200)

        replay request, response, subject, userAgent, contentNegotiationResolver

        // bind a subject, this will be the subject that will be logged out.
        ThreadContext.bind(subject)

        def controller = new ShiroLogoutController()
        controller.setContentNegotiationResolver(contentNegotiationResolver)
        controller.doPost(request, response)

        verify request, response, subject, userAgent, contentNegotiationResolver
    }
}
