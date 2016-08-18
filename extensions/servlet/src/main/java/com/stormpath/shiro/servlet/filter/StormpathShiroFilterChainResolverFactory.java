package com.stormpath.shiro.servlet.filter;

import com.stormpath.sdk.lang.Classes;
import com.stormpath.sdk.lang.Strings;
import com.stormpath.sdk.servlet.config.filter.SelfConfiguredStormpathFilter;
import com.stormpath.sdk.servlet.filter.DefaultFilterConfig;
import org.apache.shiro.config.ConfigurationException;
import org.apache.shiro.util.AbstractFactory;
import org.apache.shiro.web.filter.mgt.FilterChainResolver;

import javax.servlet.Filter;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class StormpathShiroFilterChainResolverFactory extends AbstractFactory<FilterChainResolver> {

    final private FilterChainResolver delegateFilterChainResolver;

    final private ServletContext servletContext;

    final public static String PRIORITY_FILTER_CLASSES_PARAMETER = StormpathShiroFilterChainResolverFactory.class.getName() + "_PRIORITY_FILTER_CLASSES";

    final private static Class[] DEFAULT_PRIORITY_FILTER_CLASSES = {SelfConfiguredStormpathFilter.class, StormpathShiroPassiveLoginFilter.class};

    public StormpathShiroFilterChainResolverFactory(FilterChainResolver delegateFilterChainResolver, ServletContext servletContext) {
        this.delegateFilterChainResolver = delegateFilterChainResolver;
        this.servletContext = servletContext;
    }

    @Override
    protected FilterChainResolver createInstance() {

        List<Filter> priorityFilters = new ArrayList<>();

        String priorityFilterClassNames = servletContext.getInitParameter(PRIORITY_FILTER_CLASSES_PARAMETER);
        if (Strings.hasText(priorityFilterClassNames)) {
            for (String className : Strings.commaDelimitedListToStringArray(priorityFilterClassNames)) {
                Filter filter = Classes.newInstance(className);
                priorityFilters.add(filter);
            }
        }
        else {
            priorityFilters.addAll(getDefaultFilters());
        }

        // init each filter
        for (Filter filter : priorityFilters) {
            String filterName = Strings.uncapitalize(filter.getClass().getSimpleName());
            try {
            filter.init(new DefaultFilterConfig(servletContext, filterName, Collections.<String, String>emptyMap()));
            } catch (ServletException e) {
                throw new ConfigurationException("Could not configure filter: ["+ filter.getClass().getName() +"]", e);
            }
        }

        return new ShiroPrioritizedFilterChainResolver(delegateFilterChainResolver, priorityFilters);
    }

    private List<Filter> getDefaultFilters() {
        List<Filter> priorityFilters = new ArrayList<>();
        for (Class filterClass : DEFAULT_PRIORITY_FILTER_CLASSES) {
            Filter filter = (Filter) Classes.newInstance(filterClass);
            priorityFilters.add(filter);
        }
        return priorityFilters;
    }
}
