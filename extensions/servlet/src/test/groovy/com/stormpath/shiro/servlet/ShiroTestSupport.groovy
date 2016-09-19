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

import org.apache.shiro.SecurityUtils
import org.apache.shiro.util.ThreadContext
import org.testng.annotations.AfterMethod

/**
 * Test Support class that will clear ThreadContext and static SecurityUtils.securityManager.<BR/>
 * NOTE: if you are using a static SecurityManager, you will need to add the annotation
 * <code>@Test(singleThreaded = true)</code> to your test class.
 * @since 0.7.0
 */
abstract class ShiroTestSupport {

    @AfterMethod
    public void clearSecurityManager() {
        SecurityUtils.securityManager = null
        ThreadContext.remove()
    }
}
