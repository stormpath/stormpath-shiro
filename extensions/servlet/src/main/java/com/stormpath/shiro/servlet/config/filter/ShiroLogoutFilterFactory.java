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
package com.stormpath.shiro.servlet.config.filter;

import com.stormpath.sdk.servlet.config.filter.LogoutFilterFactory;
import com.stormpath.sdk.servlet.mvc.LogoutController;
import com.stormpath.shiro.servlet.mvc.ShiroLogoutController;

/**
 * @since 0.7.0
 *
 * This will be removed before the 0.7.0 release, keeping for now as it was referenced in a support request ticket.
 * @deprecated replaced with {@link com.stormpath.shiro.servlet.filter.StormpathShiroPassiveLoginFilter}.
 */
@Deprecated
public class ShiroLogoutFilterFactory extends LogoutFilterFactory {

    @Override
    protected LogoutController newController() {
        return new ShiroLogoutController();
    }
}
