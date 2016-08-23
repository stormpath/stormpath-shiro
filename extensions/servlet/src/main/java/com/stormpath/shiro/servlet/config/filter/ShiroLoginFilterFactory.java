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

import com.stormpath.sdk.servlet.config.filter.LoginFilterFactory;
import com.stormpath.sdk.servlet.mvc.LoginController;
import com.stormpath.shiro.servlet.mvc.ShiroLoginController;

public class ShiroLoginFilterFactory extends LoginFilterFactory {

    @Override
    protected LoginController newController() {
        return new ShiroLoginController();
    }
}
