package com.stormpath.shiro.jaxrs

import com.stormpath.sdk.servlet.config.Config
import com.stormpath.sdk.servlet.filter.UnauthenticatedHandler
import com.stormpath.sdk.servlet.filter.UnauthorizedHandler
import com.stormpath.shiro.jaxrs.util.ResponseProxy
import org.apache.shiro.authz.AuthorizationException
import org.apache.shiro.authz.UnauthorizedException
import org.testng.annotations.Test

import javax.servlet.ServletContext
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import static org.easymock.EasyMock.*
import static org.testng.Assert.*
import static org.hamcrest.MatcherAssert.*
import static org.hamcrest.Matchers.*

/**
 * Tests for @{link StormpathShiroExceptionMapper}.
 */
public class StormpathShiroExceptionMapperTest {


    @Test
    def void testNullException() {

        def servletContext = mock(ServletContext)
        def servletRequest = mock(HttpServletRequest)
        def servletResponse = mock(HttpServletResponse)
        def authc = mock(UnauthenticatedHandler)
        def authz = mock(UnauthorizedHandler)

        def config = mock(Config)
        expect(servletContext.getAttribute(Config.getName())).andReturn(config)
        expect(config.getInstance("stormpath.web.authc.unauthenticatedHandler")).andReturn(authc)
        expect(config.getInstance("stormpath.web.authz.unauthorizedHandler")).andReturn(authz)
        expect(servletResponse.status).andReturn(418)

        expect(authc.onAuthenticationRequired(eq(servletRequest), anyObject(ResponseProxy))).andReturn(false)

        replay servletContext, servletRequest, servletResponse, config, authc, authz

        def mapper = new StormpathShiroExceptionMapper(servletContext, servletRequest, servletResponse)
        def response = mapper.toResponse(null)

        verify servletContext, servletRequest, servletResponse, config, authc, authz

        assertEquals response.status, 418
    }

    @Test
    def void testAuthCException() {

        def servletContext = mock(ServletContext)
        def servletRequest = mock(HttpServletRequest)
        def servletResponse = mock(HttpServletResponse)
        def authc = mock(UnauthenticatedHandler)
        def authz = mock(UnauthorizedHandler)

        def config = mock(Config)
        expect(servletContext.getAttribute(Config.getName())).andReturn(config)
        expect(config.getInstance("stormpath.web.authc.unauthenticatedHandler")).andReturn(authc)
        expect(config.getInstance("stormpath.web.authz.unauthorizedHandler")).andReturn(authz)
        expect(servletResponse.status).andReturn(418)

        expect(authc.onAuthenticationRequired(eq(servletRequest), anyObject(ResponseProxy))).andReturn(false)

        replay servletContext, servletRequest, servletResponse, config, authc, authz

        def mapper = new StormpathShiroExceptionMapper(servletContext, servletRequest, servletResponse)
        def response = mapper.toResponse(new AuthorizationException("Expected Test Exception"))

        verify servletContext, servletRequest, servletResponse, config, authc, authz

        assertEquals response.status, 418
    }

    @Test
    def void testAuthZException() {

        def servletContext = mock(ServletContext)
        def servletRequest = mock(HttpServletRequest)
        def servletResponse = mock(HttpServletResponse)
        def authc = mock(UnauthenticatedHandler)
        def authz = mock(UnauthorizedHandler)

        def config = mock(Config)
        expect(servletContext.getAttribute(Config.getName())).andReturn(config)
        expect(config.getInstance("stormpath.web.authc.unauthenticatedHandler")).andReturn(authc)
        expect(config.getInstance("stormpath.web.authz.unauthorizedHandler")).andReturn(authz)
        expect(servletResponse.status).andReturn(418)

        expect(authz.onUnauthorized(eq(servletRequest), anyObject(ResponseProxy))).andReturn(false)

        replay servletContext, servletRequest, servletResponse, config, authc, authz

        def mapper = new StormpathShiroExceptionMapper(servletContext, servletRequest, servletResponse)
        def response = mapper.toResponse(new UnauthorizedException("Expected Test Exception"))

        verify servletContext, servletRequest, servletResponse, config, authc, authz

        assertEquals response.status, 418
    }

    @Test
    def void testThrowsException() {

        def servletContext = mock(ServletContext)
        def servletRequest = mock(HttpServletRequest)
        def servletResponse = mock(HttpServletResponse)
        def authc = mock(UnauthenticatedHandler)
        def authz = mock(UnauthorizedHandler)

        def config = mock(Config)
        expect(servletContext.getAttribute(Config.getName())).andReturn(config)
        expect(config.getInstance("stormpath.web.authc.unauthenticatedHandler")).andReturn(authc)
        expect(config.getInstance("stormpath.web.authz.unauthorizedHandler")).andReturn(authz)
        expect(servletResponse.status).andReturn(418)
        servletResponse.setStatus(500)

        expect(authz.onUnauthorized(eq(servletRequest), anyObject(ResponseProxy))).andThrow(new ServletException("Expected test Exception"))

        replay servletContext, servletRequest, servletResponse, config, authc, authz

        def mapper = new StormpathShiroExceptionMapper(servletContext, servletRequest, servletResponse)
        def response = mapper.toResponse(new UnauthorizedException("Expected Test Exception"))

        verify servletContext, servletRequest, servletResponse, config, authc, authz

        assertEquals response.status, 500
    }
}
