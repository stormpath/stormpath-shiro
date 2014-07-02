/*
 * Copyright 2014 Stormpath, Inc.
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
package com.stormpath.shiro.web.service;

import com.stormpath.sdk.client.Client;
import com.stormpath.shiro.realm.ApplicationRealm;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.UnavailableSecurityManagerException;
import org.apache.shiro.mgt.RealmSecurityManager;

/**
 * @since 0.7.0
 */
public abstract class AbstractService {

    private static ApplicationRealm applicationRealm = null;

    public static final String baseUrl = "https://api.stormpath.com/v1/";

    protected ApplicationRealm getApplicationRealm() {
        if (applicationRealm == null) {
            applicationRealm = (ApplicationRealm)((RealmSecurityManager) SecurityUtils.getSecurityManager()).getRealms().iterator().next();
        }
        return applicationRealm;
    }

    protected Client getStormpathClient() {
        return getApplicationRealm().getClient();
    }

}
