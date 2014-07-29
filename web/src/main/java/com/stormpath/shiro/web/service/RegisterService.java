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

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.shiro.web.model.RegisterBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @since 0.670
 */
public class RegisterService extends AbstractService {

    private static final Logger logger = LoggerFactory.getLogger(RegisterService.class);

    private static RegisterService service = null;

    /**
     * Let's make the constructor private so we can have a single RegisterService.
     */
    private RegisterService() {
    }

    public static RegisterService getInstance() {
        if(service == null) {
            service = new RegisterService();
        }
        return service;
    }

    public void createAccount(RegisterBean registerBean) {
        Assert.notNull(registerBean, "registerBean cannot be null.");

        Application application = getStormpathClient().getResource(getApplicationRealm().getApplicationRestUrl(), Application.class);
        Account account = getStormpathClient().instantiate(Account.class);
        account.setEmail(registerBean.getEmail());
        account.setUsername(registerBean.getUsername());
        account.setPassword(registerBean.getPassword());
        account.setGivenName(registerBean.getFirstName());
        account.setMiddleName(registerBean.getMiddleName());
        account.setSurname(registerBean.getLastName());
        application.createAccount(account);
    }

}
