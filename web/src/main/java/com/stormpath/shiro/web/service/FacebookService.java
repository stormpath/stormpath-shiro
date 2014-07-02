/*
 * Copyright 2014 Stormpath, Inc.
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
package com.stormpath.shiro.web.service;

import com.stormpath.sdk.provider.CreateProviderRequest;
import com.stormpath.sdk.provider.Providers;
import com.stormpath.shiro.web.conf.Configuration;
import com.stormpath.shiro.web.utils.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Facebook-specific Singleton {@link ProviderService}.
 *
 * @since 0.6.0
 */
public class FacebookService extends ProviderService {

    private static final Logger logger = LoggerFactory.getLogger(FacebookService.class);

    private static ProviderService service = null;

    /**
     * Let's make the constructor private so we can have a single FacebookService instance.
     */
    private FacebookService() {
        super(Constants.FACEBOOK);
    }

    public static ProviderService getInstance() {
        if(service == null) {
            service = new FacebookService();
        }
        return service;
    }

    /**
     * Returns a new Facebook-based directory creation request. See
     * <a href="http://docs.stormpath.com/java/product-guide/#creating-a-facebook-directory">Creating a facebook directory</a>
     * @return a new Facebook-based directory creation request.
     */
    @Override
    protected CreateProviderRequest createProviderRequest() {
        //AppID and AppSecret are retrieved from the configuration file.
        return Providers.FACEBOOK.builder()
                .setClientId(Configuration.getFacebookAppId())
                .setClientSecret(Configuration.getFacebookAppSecret())
                .build();
    }

}
