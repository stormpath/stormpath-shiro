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
package com.stormpath.shiro.web.service

import com.stormpath.sdk.account.Account
import com.stormpath.sdk.account.AccountStatus
import com.stormpath.sdk.account.Accounts
import com.stormpath.sdk.account.CreateAccountRequest
import com.stormpath.sdk.application.Application
import com.stormpath.sdk.client.Client
import com.stormpath.shiro.realm.ApplicationRealm
import com.stormpath.shiro.web.model.RegisterBean
import org.apache.shiro.mgt.RealmSecurityManager
import org.apache.shiro.util.ThreadContext
import org.easymock.IArgumentMatcher
import org.junit.Before
import org.junit.Test

import java.lang.reflect.Field

import static org.easymock.EasyMock.*

class RegisterServiceTest {

    private RegisterBean bean;

    @Before
    public void before() {
        bean = new RegisterBean()
        bean.setEmail("someEmail")
        bean.setFirstName("firstName")
        bean.setLastName("lastName")
        bean.setMiddleName("middleName")
        bean.setPassword("myPassword")
        bean.setUsername("username")
    }

    @Test
    public void testCreateAccount() {

        def securityManager = createStrictMock(RealmSecurityManager)
        def collection = createStrictMock(Collection)
        def iterator = createStrictMock(Iterator)
        def realm = createStrictMock(ApplicationRealm)
        def client = createStrictMock(Client)
        def appHref = "https://api.stormpath.com/v1/applications/3TqjyZ2qp74wDM1gYo2H93"
        def app = createStrictMock(Application)
        def account = createStrictMock(Account)

        expect(securityManager.getRealms()).andReturn(collection)
        expect(collection.iterator()).andReturn(iterator)
        expect(iterator.next()).andReturn(realm)
        expect(realm.getClient()).andReturn(client)
        expect(realm.getApplicationRestUrl()).andReturn(appHref)
        expect(client.getResource(appHref, Application)).andReturn(app)
        expect(realm.getClient()).andReturn(client)
        expect(client.instantiate(Account)).andReturn(account)
        expect(account.setEmail(bean.email))
        expect(account.setUsername(bean.username))
        expect(account.setPassword(bean.password))
        expect(account.setGivenName(bean.firstName))
        expect(account.setMiddleName(bean.middleName))
        expect(account.setSurname(bean.lastName))
        expect(app.createAccount(account)).andReturn(account)

        replay securityManager, collection, iterator, realm, client, app, account

        ThreadContext.bind(securityManager)

        def service = RegisterService.getInstance()
        service.createAccount(bean)

        verify securityManager, collection, iterator, realm, client, app, account

        //Since service is a singleton, let's set applicationRealm to null so other tests can start with a clean instance
        setNewValue(AbstractService, service, "applicationRealm", null)
    }


    private void setNewValue(Class clazz, Object object, String fieldName, Object value){
        Field field = clazz.getDeclaredField(fieldName)
        field.setAccessible(true)
        field.set(object, value)
    }

}
