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
import com.stormpath.sdk.directory.AccountStore;
import com.stormpath.sdk.impl.api.ClientApiKey;
import com.stormpath.sdk.impl.cache.DisabledCacheManager;
import org.easymock.EasyMock;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collection;
import java.util.Collections;

import static org.easymock.EasyMock.*;

@Configuration
@EnableAutoConfiguration
public class StormpathShiroWebAutoConfigurationTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(StormpathShiroWebAutoConfigurationTestApplication.class, args);
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
        AccountStore accountStore = EasyMock.createNiceMock(AccountStore.class);

        expect(applicationList.iterator()).andReturn(applications.iterator());
        expect(application.getName()).andReturn("test-app");
        expect(application.getHref()).andReturn(appHref);
        expect(application.getDefaultAccountStore()).andReturn(accountStore);

        expect(client.getApplications()).andReturn(applicationList);
        expect(client.getResource(anyString(), eq(Application.class))).andReturn(application).anyTimes();
        expect(client.getCacheManager()).andReturn(new DisabledCacheManager());
        expect(client.getApiKey()).andReturn(new ClientApiKey("id", "secret"));

        replay(application, applicationList, client, accountStore);

        return client;
    }
}
