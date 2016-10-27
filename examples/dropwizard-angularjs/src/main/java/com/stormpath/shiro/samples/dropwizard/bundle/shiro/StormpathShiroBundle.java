package com.stormpath.shiro.samples.dropwizard.bundle.shiro;

import com.stormpath.shiro.jaxrs.StormpathShiroFeature;
import com.stormpath.shiro.servlet.env.StormpathShiroEnvironmentLoaderListener;
import io.dropwizard.ConfiguredBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.apache.shiro.web.servlet.ShiroFilter;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.glassfish.jersey.server.ResourceConfig;

import javax.servlet.DispatcherType;
import java.util.EnumSet;

/**
 * Simple Shiro Bundle, based code copied from:
 * <a href="https://github.com/silb/dropwizard-shiro/blob/master/src/main/java/org/secnod/dropwizard/shiro/ShiroBundle.java">silb/dropwizard-shiro</a>.
 */
public abstract class StormpathShiroBundle<T> implements ConfiguredBundle<T> {

    @SuppressWarnings("PMD.EmptyMethodInAbstractClassShouldBeAbstract")
    @Override
    public void initialize(Bootstrap<?> bootstrap) {
        // do nothing
    }

    @Override
    public void run(T configuration, Environment environment) {
        ShiroConfiguration shiroConfig = narrow(configuration);
        if (shiroConfig == null) {
            shiroConfig = new ShiroConfiguration();
        }
        ResourceConfig resourceConfig = environment.jersey().getResourceConfig();

        resourceConfig.register(StormpathShiroFeature.class);

        // This listener will configure Shiro and Stormpath
        environment.servlets().addServletListeners(new StormpathShiroEnvironmentLoaderListener());
        environment.servlets()
                .addServlet("default", DefaultServlet.class)
                .addMapping("/"); // The Stormpath API requires the 'default' servlet
        environment.servlets()
                .addFilter("ShiroFilter", ShiroFilter.class) // setup the Shiro Filter
                .addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), false, shiroConfig.filterUrlPattern());
    }

    /**
     * Narrow down the complete configuration to just the Shiro configuration.
     */
    protected abstract ShiroConfiguration narrow(T configuration);

}
