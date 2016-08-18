package com.stormpath.shiro.servlet.filter

import org.apache.shiro.config.ConfigurationException
import org.apache.shiro.web.filter.mgt.FilterChainResolver
import org.testng.annotations.Test

import javax.servlet.Filter
import javax.servlet.FilterChain
import javax.servlet.FilterConfig
import javax.servlet.ServletContext
import javax.servlet.ServletException
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse

import static org.testng.Assert.*
import static org.easymock.EasyMock.*
import static org.hamcrest.Matchers.*
import static org.hamcrest.MatcherAssert.*

/**
 * Tests for {@link StormpathShiroFilterChainResolverFactory}.
 */
class StormpathShiroFilterChainResolverFactoryTest {

    @Test
    public void testHappyPath() {

        def servletContext = createMock(ServletContext)
        def delegate = createMock(FilterChainResolver)

        expect(servletContext.getInitParameter(StormpathShiroFilterChainResolverFactory.PRIORITY_FILTER_CLASSES_PARAMETER)).andReturn(StormpathShiroPassiveLoginFilter.getName())

        replay servletContext, delegate

        def resolverFactory = new StormpathShiroFilterChainResolverFactory(delegate, servletContext)
        assertNotNull resolverFactory.getInstance()
        assertThat resolverFactory.getInstance(), instanceOf(ShiroPrioritizedFilterChainResolver)
        def resolver = (ShiroPrioritizedFilterChainResolver) resolverFactory.getInstance()
        assertThat resolver.priorityFilters, allOf(hasSize(1), hasItem(instanceOf(StormpathShiroPassiveLoginFilter)))

        verify servletContext, delegate
    }

    @Test
    public void testFilterInitThrowsException() {

        def servletContext = createMock(ServletContext)
        def delegate = createMock(FilterChainResolver)

        expect(servletContext.getInitParameter(StormpathShiroFilterChainResolverFactory.PRIORITY_FILTER_CLASSES_PARAMETER)).andReturn(ExceptionThrowingFilter.getName())

        replay servletContext, delegate

        def resolverFactory = new StormpathShiroFilterChainResolverFactory(delegate, servletContext)

        try {
            resolverFactory.getInstance()
            fail "Expected ConfigurationException"
        }
        catch (ConfigurationException e) {
            // expected
        }

        verify servletContext, delegate
    }

    public static class ExceptionThrowingFilter implements Filter {


        @Override
        void init(FilterConfig filterConfig) throws ServletException {
            throw new ServletException("Expected test exception")
        }

        @Override
        void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {}


        @Override
        void destroy() {}
    }
}
