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

import org.apache.shiro.config.Ini
import org.testng.annotations.Test

import static org.hamcrest.Matchers.*
import static org.hamcrest.MatcherAssert.*

/**
 * Tests for {@link IniPropertiesSource}.
 * @since 0.7.0
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
