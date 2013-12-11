/*
 * Copyright 2013 Stormpath, Inc.
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
import com.stormpath.sdk.lang.Assert;

/**
 * A Stormpath SDK {@link Cache} implementation that wraps a Shiro {@link org.apache.shiro.cache.Cache Cache} instance.
 * This allows the Stormpath SDK to use your existing Shiro caching mechanism so you only need to configure one
 * caching implementation.
 * <p/>
 * This implementation effectively acts as an adapter or bridge from the Stormpath SDK cache API to the Shiro cache API.
 *
 * @since 0.4.0
 */
public class ShiroCache<K, V> implements Cache<K, V> {

    private final org.apache.shiro.cache.Cache<K, V> SHIRO_CACHE;

    /**
     * Constructs a new {@code ShiroCache} instance that wraps (delegates to) the specified
     * Shiro {@link org.apache.shiro.cache.Cache Cache} instance.
     *
     * @param shiroCache the target Shiro cache to wrap.
     */
    public ShiroCache(final org.apache.shiro.cache.Cache<K, V> shiroCache) {
        Assert.notNull(shiroCache, "Shiro cache instance cannot be null.");
        this.SHIRO_CACHE = shiroCache;
    }

    @Override
    public V get(K key) {
        return SHIRO_CACHE.get(key);
    }

    @Override
    public V put(K key, V value) {
        return SHIRO_CACHE.put(key, value);
    }

    @Override
    public V remove(K key) {
        return SHIRO_CACHE.remove(key);
    }
}
