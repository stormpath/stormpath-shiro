package com.stormpath.shiro.servlet

import org.apache.shiro.SecurityUtils
import org.apache.shiro.util.ThreadContext
import org.testng.annotations.AfterMethod

abstract class ShiroTestSupport {

    @AfterMethod
    public void clearSecurityManager() {
        SecurityUtils.securityManager = null
        ThreadContext.remove()
    }
}
