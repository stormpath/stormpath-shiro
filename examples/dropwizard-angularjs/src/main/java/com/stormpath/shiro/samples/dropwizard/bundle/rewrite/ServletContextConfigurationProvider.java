package com.stormpath.shiro.samples.dropwizard.bundle.rewrite;

import org.ocpsoft.rewrite.config.Configuration;
import org.ocpsoft.rewrite.servlet.config.HttpConfigurationProvider;

import javax.servlet.ServletContext;


/**
 * This Configuration provider loads a rewrite-servlet configuration from a {@link ServletContext} attribute
 * 'com.stormpath.shiro.samples.dropwizard.bundle.rewrite.ServletContextConfigurationProvider.REWRITE_CONFIG'.
 * If the attribute is not found, null is returned.
 */
public class ServletContextConfigurationProvider extends HttpConfigurationProvider {

    public final static String REWRITE_CONFIG_KEY = ServletContextConfigurationProvider.class + ".REWRITE_CONFIG";

    @Override
    public Configuration getConfiguration(ServletContext context) {

        if (context != null) {
            Object rawConfig = context.getAttribute(REWRITE_CONFIG_KEY);
            if (rawConfig != null && !(rawConfig instanceof Configuration)) {
                throw new IllegalStateException("Invalid configuration attribute ["+ REWRITE_CONFIG_KEY +"] must be of type ["+ Configuration.class +"]");
            }
            return (Configuration) rawConfig;
        }

        return null;
    }

    @Override
    public int priority() {
        return 10;
    }
}
