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

import com.stormpath.sdk.directory.CustomData;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.shiro.realm.AccountCustomDataPermissionResolver;
import com.stormpath.shiro.servlet.UserUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;


/**
 * Singleton service encapsulating Custom Data operations (i.e. retrieve, delete, insert) to be executed in
 * the <a href="http://www.stormpath.com">Stormpath</a> account by means of the
 * <a href="https://github.com/stormpath/stormpath-sdk-java">Stormpath Java SDK</a>
 * @since 0.7.0
 */
public class CustomDataService extends AbstractService {
    private static final Logger logger = LoggerFactory.getLogger(CustomDataService.class);

    private static CustomDataService service = null;

    /**
     * Let's make the constructor private so we can have a single CustomDataService.
     */
    private CustomDataService() {
    }

    public static CustomDataService getInstance() {
        if(service == null) {
            service = new CustomDataService();
        }
        return service;
    }

    /**
     *  This method will insert or update a custom data field into the custom data of the given account. When a new
     *  field is added (i.e, not previously existing key) then the field is created with the key and the value as String.
     *  If the key already exists, the existing value will be converted to a list and the new value will be added to it.
     *
     *  If the key equals the <a href="https://github.com/stormpath/stormpath-shiro/wiki#permissions)">shiro permission string</a>
     *  ('apacheShiroPermissions' by default) then {@link com.stormpath.shiro.authz.CustomDataPermissionsEditor} will be used to update it.
     *
     * @param key the key of the custom data field to be added
     * @param value the value of the custom data field to be added
     */
    public void put(String key, String value) {
        CustomData customData = UserUtils.getAccount().getCustomData();
        customData.put(key, value);
        customData.save();
    }

    public void putAll(Map<String, Object> customDataEntries) {
        Assert.notNull(customDataEntries);
        CustomData customData = UserUtils.getAccount().getCustomData();
        customData.putAll(customDataEntries);
        customData.save();
    }

    /**
     * Checks whether the given key equals the "shiro permission string". See: <a href="https://github.com/stormpath/stormpath-shiro/wiki#permissions></a>
     * @param key the key to compare
     * @return 'true' if the given key equals the shiro permission string. 'False' otherwise.
     */
    private boolean isPermissionKey(String key) {
        return ((AccountCustomDataPermissionResolver)getApplicationRealm().getAccountPermissionResolver()).getCustomDataFieldName().equals(key);
    }

}
