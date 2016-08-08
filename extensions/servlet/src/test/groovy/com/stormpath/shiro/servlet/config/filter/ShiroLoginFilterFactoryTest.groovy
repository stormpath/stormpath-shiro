package com.stormpath.shiro.servlet.config.filter

import com.stormpath.shiro.servlet.ShiroTestSupport
import com.stormpath.shiro.servlet.mvc.ShiroLoginController
import org.testng.annotations.Test

import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.instanceOf


/**
 * Tests for {@link ShiroLoginFilterFactory}.
 */
class ShiroLoginFilterFactoryTest extends ShiroTestSupport {

    @Test
    public void testNewController() {
        assertThat new ShiroLoginFilterFactory().newController(), instanceOf(ShiroLoginController)
    }
}
