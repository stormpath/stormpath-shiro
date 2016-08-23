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

import com.stormpath.sdk.servlet.config.Config;
import com.stormpath.sdk.servlet.config.filter.IDSiteResultFilterFactory;
import com.stormpath.sdk.servlet.mvc.IdSiteResultController;
import com.stormpath.shiro.servlet.mvc.ShiroIDSiteResultController;
import com.stormpath.shiro.servlet.mvc.ShiroLogoutController;


public class ShiroIDSiteResultFilterFactory extends IDSiteResultFilterFactory {

    @Override
    public void doConfigure(IdSiteResultController c, Config config) {

        ShiroLogoutController controller = new ShiroLogoutController();
        controller.setNextUri(config.getLogoutConfig().getNextUri());
        controller.setInvalidateHttpSession(config.isLogoutInvalidateHttpSession());
        controller.setProduces(config.getProducedMediaTypes());
        controller.init();

        c.setLogoutController(controller);

        super.doConfigure(c, config);
    }

    @Override
    protected IdSiteResultController newController() {
        return new ShiroIDSiteResultController();
    }
}
