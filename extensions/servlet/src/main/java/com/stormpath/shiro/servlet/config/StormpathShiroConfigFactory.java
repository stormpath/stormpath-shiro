package com.stormpath.shiro.servlet.config;

import com.stormpath.sdk.impl.config.PropertiesSource;
import com.stormpath.shiro.sdk.servlet.config.impl.MultipleSourceConfigFactory;

import javax.servlet.ServletContext;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Subclass of {@link MultipleSourceConfigFactory} that adds shiro.ini properties as config properties. <BR/>
 * <BR/>
 * The Shiro {@link org.apache.shiro.config.Ini} must be available as a {@link ServletContext} attribute named
 * {@link StormpathShiroConfigFactory#SHIRO_STORMPATH_PROPERTIES_ATTRIBUTE}.
 */
public class StormpathShiroConfigFactory extends MultipleSourceConfigFactory {

    final public static String SHIRO_STORMPATH_PROPERTIES_ATTRIBUTE = StormpathShiroConfigFactory.class.getName() + "_SHIRO_STORMPATH_PROPERTIES";

    @Override
    protected Collection<PropertiesSource> getInternalPropertiesSources(ServletContext servletContext) {

        PropertiesSource shiroPropertySource = (PropertiesSource) servletContext.getAttribute(SHIRO_STORMPATH_PROPERTIES_ATTRIBUTE);

        Collection<PropertiesSource> sources = new ArrayList<PropertiesSource>();

        if (shiroPropertySource!= null) {
            sources.add(shiroPropertySource);
        }

        return sources;
    }
}
