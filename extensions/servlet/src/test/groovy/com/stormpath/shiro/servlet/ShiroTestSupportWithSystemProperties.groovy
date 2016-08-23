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
package com.stormpath.shiro.servlet

import org.testng.annotations.AfterMethod
import org.testng.annotations.Test

@Test(singleThreaded = true)
abstract class ShiroTestSupportWithSystemProperties extends ShiroTestSupport {

    private Map<String, String> rememberedSystemProperties = new HashMap<>()

    protected void setSystemProperty(String key, String value) {

        // remember the old one
        rememberedSystemProperties.put(key, System.getProperty(key))

        // set the new one
        System.setProperty(key, value)
    }

    @AfterMethod
    public void restoreSystemProperties() {

        for (Map.Entry<String, String> entry : rememberedSystemProperties) {
            if (entry.value == null) {
                System.clearProperty(entry.key)
            }
            else {
                System.setProperty(entry.value)
            }
        }
    }



}
