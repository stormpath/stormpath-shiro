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
package com.stormpath.shiro.cache

import org.testng.annotations.Test

import static org.easymock.EasyMock.*
import static org.testng.Assert.assertNotNull
import static org.testng.Assert.assertSame

/**
 * @since 0.4.0
 */
class ShiroCacheManagerTest {

    @Test(expectedExceptions = IllegalArgumentException)
    void testNullCacheManager() {
        new ShiroCacheManager(null)
    }

    @Test
    void testGetCache() {

        def shiroCacheManager = createStrictMock(org.apache.shiro.cache.CacheManager)
        def shiroCache = createStrictMock(org.apache.shiro.cache.Cache)

        def cacheName = 'name'

        expect(shiroCacheManager.getCache(same(cacheName))).andReturn shiroCache

        replay shiroCache, shiroCacheManager

        ShiroCacheManager cacheManager = new ShiroCacheManager(shiroCacheManager)
        def cache = cacheManager.getCache(cacheName)
        assertNotNull(cache)
        assertSame shiroCache, cache.SHIRO_CACHE

        verify shiroCache, shiroCacheManager
    }

}
