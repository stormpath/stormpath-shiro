package com.stormpath.shiro.servlet.config;

import com.stormpath.sdk.servlet.config.Config;
import com.stormpath.sdk.servlet.config.ConfigLoader;
import org.apache.shiro.config.Ini;

import javax.servlet.ServletContext;

import java.util.Collections;

import static com.stormpath.shiro.servlet.config.AppendingConfigFactory.SHIRO_STORMPATH_ADDITIONAL_PROPERTIES_ATTRIBUTE;


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
