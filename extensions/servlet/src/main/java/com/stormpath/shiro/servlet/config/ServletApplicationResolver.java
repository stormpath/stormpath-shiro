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


import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.client.Client;
import com.stormpath.sdk.servlet.application.ApplicationLoader;
import com.stormpath.sdk.servlet.client.ClientLoader;
import com.stormpath.shiro.realm.ApplicationResolver;

import javax.servlet.ServletContext;

public class ServletApplicationResolver implements ApplicationResolver {

    private ServletContext servletContext = null;

    @Override
    public Application getApplication(Client client, String href) {

        servletContext.setAttribute(ClientLoader.CLIENT_ATTRIBUTE_KEY, client);
        Application application = com.stormpath.sdk.servlet.application.ApplicationResolver.INSTANCE.getApplication(servletContext);
        servletContext.setAttribute(ApplicationLoader.APP_ATTRIBUTE_NAME, application);
        return application;
    }

    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }
}
