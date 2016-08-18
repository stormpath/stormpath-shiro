package com.stormpath.shiro.servlet.filter

import org.apache.shiro.web.filter.mgt.FilterChainResolver
import org.apache.shiro.web.servlet.ProxiedFilterChain
import org.testng.Assert
import org.testng.annotations.Test

import javax.servlet.Filter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import static org.easymock.EasyMock.*
import static org.hamcrest.Matchers.*
import static org.hamcrest.MatcherAssert.*
import static org.testng.Assert.*


class ShiroPrioritizedFitlerChainResolverTest {

    @Test
    public void testDelegateChainIsNull() {

        def request = createMock(HttpServletRequest)
        def response = createMock(HttpServletResponse)
        def chain = createMock(FilterChain)
        def delegateFilterChainResolver = createMock(FilterChainResolver)

        expect(delegateFilterChainResolver.getChain(request, response, chain)).andReturn(null)
        replay request, response, chain, delegateFilterChainResolver

        def filterChainResolver = new ShiroPrioritizedFilterChainResolver(delegateFilterChainResolver, null)
        def result = filterChainResolver.getChain(request, response, chain)

        verify request, response, chain, delegateFilterChainResolver

        assertSame result, chain
    }

    @Test
    public void testDelegateChainIsNullWithPriorityFilter() {

        def request = createMock(HttpServletRequest)
        def response = createMock(HttpServletResponse)
        def chain = createMock(FilterChain)
        def delegateFilterChainResolver = createMock(FilterChainResolver)
        def filter = createMock(Filter)

        expect(delegateFilterChainResolver.getChain(request, response, chain)).andReturn(null)
        replay request, response, chain, delegateFilterChainResolver, filter

        def priorityFilters = Collections.singletonList(filter)
        def filterChainResolver = new ShiroPrioritizedFilterChainResolver(delegateFilterChainResolver, priorityFilters)
        def result = filterChainResolver.getChain(request, response, chain)

        verify request, response, chain, delegateFilterChainResolver, filter

        assertThat result, instanceOf(ProxiedFilterChain)
        assertSame priorityFilters, result.filters
    }
}
