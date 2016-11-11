package com.stormpath.shiro.jaxrs.util

import org.testng.annotations.Test

import javax.servlet.http.HttpServletResponse

import static org.easymock.EasyMock.*
import static org.testng.Assert.*
import static org.hamcrest.MatcherAssert.*
import static org.hamcrest.Matchers.*

/**
 * Tests for @{link ResponseProxy}.
 */
class ResponseProxyTest {

    @Test
    def void testSendRedirect() {

        def servletResponse = mock(HttpServletResponse)
        expect(servletResponse.status).andReturn(200)
        expect(servletResponse.status).andReturn(302)
        servletResponse.sendRedirect("/redirect_location")

        replay servletResponse

        def responseProxy = new ResponseProxy(servletResponse)
        responseProxy.sendRedirect("/redirect_location")
        def response = responseProxy.toResponse()

        verify servletResponse

        assertEquals response.status, 302
        assertEquals response.location.toString(), "/redirect_location"
    }

    @Test
    def void testSetDateHeader() {

        long date = new Date().time

        def servletResponse = mock(HttpServletResponse)
        expect(servletResponse.status).andReturn(200)
        servletResponse.setDateHeader("date-long", date)

        replay servletResponse

        def responseProxy = new ResponseProxy(servletResponse)
        responseProxy.setDateHeader("date-long", date)
        def response = responseProxy.toResponse()

        verify servletResponse

        assertEquals response.status, 200
        assertEquals response.headers.getFirst("date-long"), new Date(date)
    }

    @Test
    def void testAddDateHeader() {

        long date1 = new Date().time
        long date2 = new Date().time

        def servletResponse = mock(HttpServletResponse)
        expect(servletResponse.status).andReturn(200)
        servletResponse.setDateHeader("date-long", date1)
        servletResponse.addDateHeader("date-long", date2)

        replay servletResponse

        def responseProxy = new ResponseProxy(servletResponse)
        responseProxy.setDateHeader("date-long", date1)
        responseProxy.addDateHeader("date-long", date2)
        def response = responseProxy.toResponse()

        verify servletResponse

        assertEquals response.status, 200
        assertThat response.headers.get("date-long"), allOf(hasItem(new Date(date1)), hasItem(new Date(date2)), hasSize(2))
    }

    @Test
    def void testSetHeader() {

        def header = "foobar"

        def servletResponse = mock(HttpServletResponse)
        expect(servletResponse.status).andReturn(200)
        servletResponse.setHeader("test-string", header)

        replay servletResponse

        def responseProxy = new ResponseProxy(servletResponse)
        responseProxy.setHeader("test-string", header)
        def response = responseProxy.toResponse()

        verify servletResponse

        assertEquals response.status, 200
        assertEquals response.headers.getFirst("test-string"), header
    }

    @Test
    def void testAddHeader() {

        def header1 = "foobar1"
        def header2 = "foobar2"

        def servletResponse = mock(HttpServletResponse)
        expect(servletResponse.status).andReturn(200)
        servletResponse.setHeader("test-string", header1)
        servletResponse.addHeader("test-string", header2)

        replay servletResponse

        def responseProxy = new ResponseProxy(servletResponse)
        responseProxy.setHeader("test-string", header1)
        responseProxy.addHeader("test-string", header2)
        def response = responseProxy.toResponse()

        verify servletResponse

        assertEquals response.status, 200
        assertThat response.headers.get("test-string"), allOf(hasItem(header1), hasItem(header2), hasSize(2))
    }

    @Test
    def void testSetIntHeader() {

        def header = 11

        def servletResponse = mock(HttpServletResponse)
        expect(servletResponse.status).andReturn(200)
        servletResponse.setIntHeader("test-int", header)

        replay servletResponse

        def responseProxy = new ResponseProxy(servletResponse)
        responseProxy.setIntHeader("test-int", header)
        def response = responseProxy.toResponse()

        verify servletResponse

        assertEquals response.status, 200
        assertEquals response.headers.getFirst("test-int"), header
    }

    @Test
    def void testAddIntHeader() {

        def header1 = 11
        def header2 = 22

        def servletResponse = mock(HttpServletResponse)
        expect(servletResponse.status).andReturn(200)
        servletResponse.setIntHeader("test-int", header1)
        servletResponse.addIntHeader("test-int", header2)

        replay servletResponse

        def responseProxy = new ResponseProxy(servletResponse)
        responseProxy.setIntHeader("test-int", header1)
        responseProxy.addIntHeader("test-int", header2)
        def response = responseProxy.toResponse()

        verify servletResponse

        assertEquals response.status, 200
        assertThat response.headers.get("test-int"), allOf(hasItem(header1), hasItem(header2), hasSize(2))
    }

    @Test
    def void testSetStatus() {

        def servletResponse = mock(HttpServletResponse)
        expect(servletResponse.status).andReturn(200)
        servletResponse.setStatus(404)

        replay servletResponse

        def responseProxy = new ResponseProxy(servletResponse)
        responseProxy.setStatus(404)
        def response = responseProxy.toResponse()

        verify servletResponse

        assertEquals response.status, 404
    }

}
