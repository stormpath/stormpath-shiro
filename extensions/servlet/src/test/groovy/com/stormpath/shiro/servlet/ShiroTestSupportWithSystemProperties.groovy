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
