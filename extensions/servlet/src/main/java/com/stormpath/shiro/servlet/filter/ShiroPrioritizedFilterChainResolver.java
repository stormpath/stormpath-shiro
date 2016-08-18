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
