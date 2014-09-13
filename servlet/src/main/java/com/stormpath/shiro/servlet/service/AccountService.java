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

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.lang.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides
 *
 * @since 0.7.0
 */
public class AccountService extends AbstractService {

    private static final Logger logger = LoggerFactory.getLogger(AccountService.class);

    private static AccountService service = null;

    /**
     * Let's make the constructor private so we can have a single AccountService.
     */
    private AccountService() {
    }

    public static AccountService getInstance() {
        if(service == null) {
            service = new AccountService();
        }
        return service;
    }

    public Account getAccount(String accountHref) {
        Assert.hasText(accountHref);
        return getStormpathClient().getResource(accountHref, Account.class);
    }

}
