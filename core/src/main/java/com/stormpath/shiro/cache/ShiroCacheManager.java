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
package com.stormpath.shiro.cache;

import com.stormpath.sdk.cache.Cache;
import com.stormpath.sdk.cache.CacheManager;
import com.stormpath.sdk.lang.Assert;

/**
 * A Stormpath SDK {@link CacheManager} implementation that wraps a Shiro
 * {@link org.apache.shiro.cache.CacheManager CacheManager} instance.  This allows the Stormpath SDK to use your
 * existing Shiro caching mechanism so you only need to configure one caching implementation.
 * <p/>
 * This implementation effectively acts as an adapter or bridge from the Stormpath SDK cacheManager API to the Shiro
 * cacheManager API.
 *
 * @since 0.4.0
 */
public class ShiroCacheManager implements CacheManager {

    private final org.apache.shiro.cache.CacheManager SHIRO_CACHE_MANAGER;

    /**
     * Constructs a new {@code ShiroCacheManager} instance that wraps (delegates to) the specified
     * Shiro {@link org.apache.shiro.cache.CacheManager CacheManager} instance.
     *
     * @param shiroCacheManager the target Shiro cache manager to wrap.
     */
    public ShiroCacheManager(org.apache.shiro.cache.CacheManager shiroCacheManager) {
        Assert.notNull(shiroCacheManager, "Shiro CacheManager instance cannot be null.");
        this.SHIRO_CACHE_MANAGER = shiroCacheManager;
    }

    /**
     * Consults the wrapped Shiro {@link org.apache.shiro.cache.CacheManager CacheManager} instance to obtain a
     * named Shiro {@link org.apache.shiro.cache.Cache Cache} instance.  The instance is wrapped and returned as a
     * {@link ShiroCache} instance, which acts as a bridge/adapter over Shiro's existing Cache API.
     *
     * @param name the name of the cache to acquire.
     * @param <K>  The cache key type
     * @param <V>  The cache value type
     * @return the Cache with the given name
     */
    @Override
    public <K, V> Cache<K, V> getCache(String name) {
        final org.apache.shiro.cache.Cache<K, V> shiroCache = SHIRO_CACHE_MANAGER.getCache(name);
        return new ShiroCache<K, V>(shiroCache);
    }
}
