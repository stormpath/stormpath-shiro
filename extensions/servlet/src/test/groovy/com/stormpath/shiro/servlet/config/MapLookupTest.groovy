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
package com.stormpath.shiro.servlet.config

import org.testng.annotations.Test

import static org.testng.Assert.*

/**
 * Tests for {@link MapLookup}.
 *
 * @since 0.8
 */
class MapLookupTest {

    private Map<String, String> backingMap = new HashMap<>();

    MapLookupTest() {
        backingMap.put("key1", "value1")
        backingMap.put("key2", "value2")
        backingMap.put("null-value", null)
    }

    @Test
    void testNullLookup() {
        assertNull new MapLookup(null).lookup(null)
        assertNull new MapLookup(null).lookup("nothing")
        assertNull new MapLookup(backingMap).lookup("nothing")
        assertNull new MapLookup(backingMap).lookup(null)
        assertNull new MapLookup(backingMap).lookup("null-value")

        Map<String, String> backingMap = new HashMap<>();
        backingMap.put(null, "null-key")
        assertEquals "null-key", new MapLookup(backingMap).lookup(null)
    }

    @Test
    void testNormalLookup() {
        assertEquals "value1", new MapLookup(backingMap).lookup("key1")
        assertEquals "value2", new MapLookup(backingMap).lookup("key2")
    }

}
