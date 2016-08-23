package com.stormpath.shiro.servlet.config;

import com.stormpath.sdk.impl.config.PropertiesSource;
import com.stormpath.sdk.servlet.config.Config;
import com.stormpath.sdk.servlet.config.impl.DefaultConfigFactory;
import org.apache.shiro.config.ConfigurationException;
import org.apache.shiro.util.CollectionUtils;

import javax.servlet.ServletContext;
import java.util.Collection;

public class AppendingConfigFactory extends DefaultConfigFactory {

    final public static String SHIRO_STORMPATH_ADDITIONAL_PROPERTIES_ATTRIBUTE = AppendingConfigFactory.class.getName() + "_SHIRO_STORMPATH_ADDITIONAL_PROPERTIES";

    @Override
    public Config createConfig(ServletContext servletContext) {
        Config config = super.createConfig(servletContext);
        return appendConfig(config, servletContext);
    }

    private Config appendConfig(Config config, ServletContext servletContext) {

        Collection<PropertiesSource> additionalPropertiesSources = getPropertiesSources(servletContext);
        if (!CollectionUtils.isEmpty(additionalPropertiesSources)) {
            for(PropertiesSource propertiesSource : additionalPropertiesSources) {
                config.putAll(propertiesSource.getProperties());
            }
        }
        return config;
    }

    private Collection<PropertiesSource> getPropertiesSources(ServletContext servletContext) {

        Object attribute = servletContext.getAttribute(SHIRO_STORMPATH_ADDITIONAL_PROPERTIES_ATTRIBUTE);
        if (attribute != null && !(attribute instanceof Collection)) {
            throw new ConfigurationException("Servlet Context attribute: '" + SHIRO_STORMPATH_ADDITIONAL_PROPERTIES_ATTRIBUTE +"' should have been a collection, but was: '"+ attribute.getClass() +"'");
        }

        return (Collection<PropertiesSource>) attribute;

    }
}
