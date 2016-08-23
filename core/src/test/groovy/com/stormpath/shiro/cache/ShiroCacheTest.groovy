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
import static org.testng.Assert.assertSame

/**
 * @since 0.4.0
 */
class ShiroCacheTest {

    @Test(expectedExceptions = IllegalArgumentException)
    void testNullShiroCache() {
        new ShiroCache(null)
    }

    @Test
    void testGet() {

        def shiroCache = createStrictMock(org.apache.shiro.cache.Cache)

        def key = 'key'
        def value = 'value'

        expect(shiroCache.get(key)).andReturn value

        replay shiroCache

        def cache = new ShiroCache(shiroCache)
        def retval = cache.get(key)

        assertSame value, retval

        verify shiroCache
    }

    @Test
    void testPut() {

        def shiroCache = createStrictMock(org.apache.shiro.cache.Cache)

        def key = 'key'
        def value = 'value1'
        def prev = 'value0'

        expect(shiroCache.put(same(key), same(value))).andReturn prev

        replay shiroCache

        def cache = new ShiroCache(shiroCache)

        def retval = cache.put(key, value)

        assertSame prev, retval

        verify shiroCache
    }

    @Test
    void testRemove() {

        def shiroCache = createStrictMock(org.apache.shiro.cache.Cache)

        def key = 'key'
        def prev = 'value0'

        expect(shiroCache.remove(key)).andReturn prev

        replay shiroCache

        def cache = new ShiroCache(shiroCache)

        def retval = cache.remove(key)

        assertSame prev, retval

        verify shiroCache
    }

}
