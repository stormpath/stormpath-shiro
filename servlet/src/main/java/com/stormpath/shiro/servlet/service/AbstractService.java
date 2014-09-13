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
package com.stormpath.shiro.servlet.service;

import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.client.Client;
import com.stormpath.shiro.realm.ApplicationRealm;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.mgt.RealmSecurityManager;

/**
 * Abstract class that should be overriden by any class that integrates this app with Stormpath's Backend. This
 * class provides easy access to the {@link com.stormpath.shiro.realm.ApplicationRealm ApplicationRealm}, the
 * {@link com.stormpath.sdk.client.Client Stormpath Client instance} and the configured
 * Stormpath {@link com.stormpath.sdk.application.Application application} this Shiro app interacts with.
 *
 * @since 0.7.0
 */
public abstract class AbstractService {

    private static ApplicationRealm applicationRealm = null;

    private static Application application = null;

    /**
     * Returns the {@link com.stormpath.shiro.realm.ApplicationRealm Stormpath Shiro Plugin Application Realm}.
     *
     * @return the {@link com.stormpath.shiro.realm.ApplicationRealm Stormpath Shiro Plugin Application Realm}.
     */
    protected ApplicationRealm getApplicationRealm() {
        if (applicationRealm == null) {
            applicationRealm = (ApplicationRealm)((RealmSecurityManager) SecurityUtils.getSecurityManager()).getRealms().iterator().next();
        }
        return applicationRealm;
    }

    /**
     * Returns the {@link com.stormpath.sdk.client.Client Stormpath Client instance} this Shiro App is using to interact with
     * the Stormpath Backend.
     *
     * @return the {@link com.stormpath.sdk.client.Client Stormpath Client instance} this Shiro App is using to interact with
     * the Stormpath Backend.
     */
    protected Client getStormpathClient() {
        return getApplicationRealm().getClient();
    }

    /**
     * Returns the {@link com.stormpath.sdk.application.Application Stormpath Application instance} this Shiro App is configured
     * to work with.
     * <p/>
     * The Application instance is static and thus shared among every sub-class of this class.
     *
     * @return the {@link com.stormpath.sdk.application.Application Stormpath Application instance} this Shiro App is configured
     * to work with.
     */
    protected Application getStormpathApplication() {
        if (this.application == null) {
            this.application = getStormpathClient().getResource(getApplicationRealm().getApplicationRestUrl(), Application.class);
        }
        return this.application;
    }

}
