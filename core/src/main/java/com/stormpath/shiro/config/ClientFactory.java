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
package com.stormpath.shiro.config;

import com.stormpath.sdk.client.Client;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.util.Factory;


public interface ClientFactory extends Factory<Client> {

    void setApiKeyFileLocation(String apiKeyFileLocation);

    void setApiKeyId(String apiKeyId);

    void setApiKeySecret(String apiKeySecret);

    void setBaseUrl(String baseUrl);

    void setCacheManager(CacheManager cacheManager);

}
