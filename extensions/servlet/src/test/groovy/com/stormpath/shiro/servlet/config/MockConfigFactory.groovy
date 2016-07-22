package com.stormpath.shiro.servlet.config;


import com.stormpath.sdk.servlet.config.Config
import com.stormpath.sdk.servlet.config.ConfigFactory
import org.easymock.EasyMock

import javax.servlet.ServletContext;

public class MockConfigFactory implements ConfigFactory {

    @Override
    public Config createConfig(ServletContext servletContext) {

        return EasyMock.createMock(Config)
    }
}
