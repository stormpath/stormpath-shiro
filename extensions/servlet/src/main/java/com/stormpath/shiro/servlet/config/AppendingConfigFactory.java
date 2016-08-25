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
package com.stormpath.shiro.servlet.config;

import com.stormpath.sdk.impl.config.PropertiesSource;
import com.stormpath.sdk.servlet.config.Config;
import com.stormpath.sdk.servlet.config.impl.DefaultConfigFactory;
import org.apache.shiro.config.ConfigurationException;
import org.apache.shiro.util.CollectionUtils;

import javax.servlet.ServletContext;
import java.util.Collection;

/**
 * @since 0.7.0
 */
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
