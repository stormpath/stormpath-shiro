package com.stormpath.shiro.servlet.config.filter

import com.stormpath.shiro.servlet.ShiroTestSupport
import com.stormpath.shiro.servlet.mvc.ShiroLogoutController
import org.testng.annotations.Test

import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.instanceOf

/**
 * Tests for {@link ShiroLogoutFilterFactory}.
 */
class ShiroLogoutFilterFactoryTest extends ShiroTestSupport {

    @Test
    public void testNewController() {
        assertThat new ShiroLogoutFilterFactory().newController(), instanceOf(ShiroLogoutController)
    }
}
