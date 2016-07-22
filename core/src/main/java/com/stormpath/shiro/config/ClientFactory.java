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
