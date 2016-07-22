package com.stormpath.shiro.servlet.config

import org.apache.shiro.config.Ini
import org.testng.annotations.Test

import static org.hamcrest.Matchers.*
import static org.hamcrest.MatcherAssert.*

/**
 * Tests for {@link IniPropertiesSource}.
 */
class IniPropertiesSourceTest {


    @Test
    public void testGetProperties() {

        assertThat new IniPropertiesSource(null).properties, anEmptyMap()

        Ini ini = new Ini()
        assertThat new IniPropertiesSource(ini).properties, anEmptyMap()

        ini.setSectionProperty(Ini.DEFAULT_SECTION_NAME, "foo", "bar")
        assertThat new IniPropertiesSource(ini).properties, anEmptyMap()

        ini.setSectionProperty("stormpath", "foo.stormpath", "bar")
        assertThat new IniPropertiesSource(ini).properties, anEmptyMap()

        ini.setSectionProperty("stormpath", "stormpath.foo", "bar")
        assertThat new IniPropertiesSource(ini).properties, allOf(aMapWithSize(1), hasEntry("stormpath.foo", "bar"))

        ini = new Ini()
        ini.setSectionProperty("other", "key", "value")
        assertThat new IniPropertiesSource(ini).properties, anEmptyMap()
    }

}
