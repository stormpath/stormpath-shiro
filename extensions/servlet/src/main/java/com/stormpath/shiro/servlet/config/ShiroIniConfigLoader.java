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

import com.stormpath.sdk.servlet.config.Config;
import com.stormpath.sdk.servlet.config.ConfigLoader;
import org.apache.shiro.config.Ini;

import javax.servlet.ServletContext;

import java.util.Collections;

import static com.stormpath.shiro.servlet.config.AppendingConfigFactory.SHIRO_STORMPATH_ADDITIONAL_PROPERTIES_ATTRIBUTE;

/**
 * Extends ConfigLoader in order to change the default config factory to {@link AppendingConfigFactory} and include
 * additional properties from a <code>shiro.ini</code> into the {@link Config}.<BR/><BR/>
 *
 * NOTE: if the servlet init parameter 'stormpathConfigFactoryClass' is already set this class functions like it's
 * parent.
 *
 * @since 0.7.0
 */
public class ShiroIniConfigLoader extends ConfigLoader {

    final private Ini ini;

    public ShiroIniConfigLoader(Ini ini) {
        this.ini = ini;
    }

    public Config createConfig(ServletContext servletContext) throws IllegalStateException {

        String className = servletContext.getInitParameter(CONFIG_FACTORY_CLASS_PARAM_NAME);

        if (className == null) {
            servletContext.setInitParameter(CONFIG_FACTORY_CLASS_PARAM_NAME, AppendingConfigFactory.class.getName());
        }
        servletContext.setAttribute(SHIRO_STORMPATH_ADDITIONAL_PROPERTIES_ATTRIBUTE, Collections.singletonList(new IniPropertiesSource(ini)));

        return super.createConfig(servletContext);
    }

    @Override
    public void destroyConfig(ServletContext servletContext) {
        super.destroyConfig(servletContext);
        servletContext.removeAttribute(SHIRO_STORMPATH_ADDITIONAL_PROPERTIES_ATTRIBUTE);
    }
}
