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


import com.stormpath.sdk.impl.config.PropertiesSource;
import org.apache.shiro.config.Ini;
import org.apache.shiro.util.CollectionUtils;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

/**
 * A {@link PropertiesSource} that returns all <code>stormpath.*</code> attributes contained withing the
 * <code>[stormpath]</code> section of an {@link Ini} object (typically a shiro.ini file).
 *
 * @since 0.7.0
 */
public class IniPropertiesSource implements PropertiesSource {

    final private Ini ini;
    final private static String STORMPATH_CONFIG_SECTION_NAME = "stormpath";

    public IniPropertiesSource(Ini ini) {
        this.ini = ini;
    }

    @Override
    public Map<String, String> getProperties() {

        if (CollectionUtils.isEmpty(ini)) {
            return Collections.emptyMap();
        }

        // find the 'stormpath' config section, otherwise return an empty map
        Ini.Section section = ini.getSection(STORMPATH_CONFIG_SECTION_NAME);
        if (CollectionUtils.isEmpty(section)) {
            section = ini.getSection(Ini.DEFAULT_SECTION_NAME);
        }

        if (CollectionUtils.isEmpty(section)) {
            return Collections.emptyMap();
        }

        // we now have a Ini section with some content
        TreeMap<String, String> allProperties = new TreeMap<String, String>(section);
        String keyPrefix = "stormpath.";
        return Collections.unmodifiableMap(allProperties.subMap(keyPrefix, keyPrefix + Character.MAX_VALUE));

    }
}
