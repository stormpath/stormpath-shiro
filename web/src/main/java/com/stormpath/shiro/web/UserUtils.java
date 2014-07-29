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
package com.stormpath.shiro.web;

import com.stormpath.sdk.account.Account;
import com.stormpath.shiro.web.service.AccountService;
import org.apache.commons.collections.MapUtils;
import org.apache.shiro.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.shiro.util.ThreadContext;

import java.util.Map;

/**
 * @since 0.7.0
 */
public class UserUtils {
    private static final Logger logger = LoggerFactory.getLogger(UserUtils.class);

    public static final String USER_UTILS_KEY = ThreadContext.class.getName() + "_USER_UTILS_KEY";

    public static Map<String, Object> getUserInfo() {
        Map<String, Object> userInfo = (Map<String, Object>) org.apache.shiro.util.ThreadContext.get(USER_UTILS_KEY);
        if (MapUtils.isEmpty(userInfo)) {
            if (SecurityUtils.getSubject().isAuthenticated()) {
                userInfo = SecurityUtils.getSubject().getPrincipals().oneByType(java.util.Map.class);
            }
            SecurityUtils.getSubject().getSession().setAttribute(USER_UTILS_KEY, userInfo);
        }
        return userInfo;
    }

    public static Account getAccount() {
        return AccountService.getInstance().getAccount(getAccountHref());
    }

    private static String getAccountHref() {
        return (String) SecurityUtils.getSubject().getPrincipal();
    }

}
