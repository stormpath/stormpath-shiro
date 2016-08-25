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
package com.stormpath.shiro.servlet.mvc;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.servlet.mvc.WebHandler;
import com.stormpath.shiro.realm.PassthroughApplicationRealm;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.config.ConfigurationException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class ShiroLoginHandler implements WebHandler {
    @Override
    public boolean handle(HttpServletRequest request, HttpServletResponse response, Account account) {

        AuthenticationToken token = new PassthroughApplicationRealm.AccountAuthenticationToken(account);

        try {
            SecurityUtils.getSubject().login(token);
        } catch (AuthenticationException e) {
            String msg = "Unable to pass on authentication info.";
            throw new ConfigurationException(msg, e);
        }

        return true;
    }
}
