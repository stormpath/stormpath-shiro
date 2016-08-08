package com.stormpath.shiro.servlet.config;


import com.stormpath.sdk.impl.config.PropertiesSource;
import org.apache.shiro.config.Ini;
import org.apache.shiro.util.CollectionUtils;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

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
