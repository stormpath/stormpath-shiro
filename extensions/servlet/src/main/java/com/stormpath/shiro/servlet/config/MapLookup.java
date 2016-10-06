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
package com.stormpath.shiro.servlet.config;

import org.apache.commons.configuration2.interpol.Lookup;

import java.util.Map;

/**
 * Simple {@link java.util.Map} implementation of {@link Lookup}.
 * @since 0.8
 */
public class MapLookup implements Lookup {

    final private Map<String, String> propertiesMap;

    public MapLookup(Map<String, String> propertiesMap) {
        this.propertiesMap = propertiesMap;
    }

    @Override
    public Object lookup(String key) {
        if (propertiesMap == null) {
            return null;
        }
        final Object obj = propertiesMap.get(key);
        if (obj == null) {
            return null;
        }
        return obj.toString();
    }
}
