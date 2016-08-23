/*
 * Copyright 2012 Stormpath, Inc.
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
