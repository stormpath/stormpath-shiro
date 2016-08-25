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
package com.stormpath.shiro.servlet.filter;


import org.apache.shiro.util.Assert;
import org.apache.shiro.util.CollectionUtils;
import org.apache.shiro.web.filter.mgt.FilterChainResolver;
import org.apache.shiro.web.servlet.ProxiedFilterChain;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.util.List;

/**
 * A {@link FilterChainResolver} that executes a list of priority filters before the consulting the delegate
 * FilterChainResolver.
 *
 * @since 0.7.0
 */
public class ShiroPrioritizedFilterChainResolver implements FilterChainResolver {

    private final FilterChainResolver delegate;
    private final List<Filter> priorityFilters;

    public ShiroPrioritizedFilterChainResolver(FilterChainResolver delegate, List<Filter> priorityFilters) {
        Assert.notNull(delegate, "Delegate FilterChainResolver cannot be null.");
        this.delegate = delegate;
        this.priorityFilters = priorityFilters;
    }

    @Override
    public FilterChain getChain(ServletRequest request, ServletResponse response, FilterChain chain) {

        FilterChain target = delegate.getChain(request, response, chain);
        if (target == null) {
            target = chain;
        }

        if (CollectionUtils.isEmpty(priorityFilters)) {
            return target;
        }

        return new ProxiedFilterChain(target, priorityFilters);
    }
}
