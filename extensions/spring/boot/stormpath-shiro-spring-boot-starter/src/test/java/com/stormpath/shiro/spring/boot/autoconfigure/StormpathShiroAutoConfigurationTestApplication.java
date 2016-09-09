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
package com.stormpath.shiro.spring.boot.autoconfigure;

import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.application.ApplicationList;
import com.stormpath.sdk.client.Client;
import com.stormpath.sdk.impl.cache.DisabledCacheManager;
import org.easymock.EasyMock;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collection;
import java.util.Collections;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;

@Configuration
@EnableAutoConfiguration
public class StormpathShiroAutoConfigurationTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(StormpathShiroAutoConfigurationTestApplication.class, args);
    }

    /**
     * Override the default to block outbound requests.
     * @return a mock client
     */
    @Bean
    @SuppressWarnings("Duplicates")
    public Client stormpathClient() {

        String appHref = "http://test-app-href";

        Application application = EasyMock.createMock(Application.class);
        Collection<Application> applications = Collections.singletonList(application);
        ApplicationList applicationList = EasyMock.createMock(ApplicationList.class);
        Client client = EasyMock.createNiceMock(Client.class);

        expect(applicationList.iterator()).andReturn(applications.iterator());
        expect(application.getName()).andReturn("test-app");
        expect(application.getHref()).andReturn(appHref);

        expect(client.getApplications()).andReturn(applicationList);
        expect(client.getResource(appHref, Application.class)).andReturn(application);

        replay(application, applicationList, client);

        return client;
    }
}
