package com.stormpath.shiro.servlet.config

import com.stormpath.sdk.client.Client
import com.stormpath.sdk.servlet.config.Config
import com.stormpath.sdk.servlet.config.impl.DefaultConfigFactory
import com.stormpath.shiro.servlet.ShiroTestSupport
import org.easymock.IAnswer

import javax.servlet.ServletContext

import static org.easymock.EasyMock.*

class StormpathConfigTestSupport extends ShiroTestSupport {



    Config config
    ServletContext servletContext



    protected void setupMocks() {

        final def delayedInitMap = new HashMap<String, Object>()
        final def configKey = "config"
        final def clientKey = "client"

        servletContext = mock(ServletContext)
        expect(servletContext.getInitParameter(DefaultConfigFactory.STORMPATH_PROPERTIES_SOURCES)).andReturn(null)
        expect(servletContext.getInitParameter(DefaultConfigFactory.STORMPATH_PROPERTIES)).andReturn(null)
        expect(servletContext.getResourceAsStream(anyObject())).andReturn(null).anyTimes()


        expect(servletContext.getAttribute(Config.getName())).andAnswer(new IAnswer<Object>() {
            @Override
            Object answer() throws Throwable {
                return delayedInitMap.get(configKey)
            }
        }).anyTimes()

        def client = mock(Client)
        expect(servletContext.getAttribute(Client.getName())).andReturn(client)

        replay servletContext, client

        config = new DefaultConfigFactory().createConfig(servletContext)
        delayedInitMap.put(configKey, config)
    }


}
