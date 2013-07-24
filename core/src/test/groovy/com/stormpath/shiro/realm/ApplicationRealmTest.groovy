/*
 * Copyright 2013 Stormpath, Inc.
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
package com.stormpath.shiro.realm

import com.stormpath.sdk.account.Account
import com.stormpath.sdk.application.Application
import com.stormpath.sdk.authc.AuthenticationRequest
import com.stormpath.sdk.authc.AuthenticationResult
import com.stormpath.sdk.client.Client
import com.stormpath.sdk.ds.DataStore
import com.stormpath.sdk.resource.ResourceException
import org.apache.shiro.authc.AuthenticationException
import org.apache.shiro.authc.SimpleAuthenticationInfo
import org.apache.shiro.authc.UsernamePasswordToken
import org.easymock.IAnswer
import org.junit.Before
import org.junit.Test
import static org.junit.Assert.*
import static org.easymock.EasyMock.*

/**
 * @since 0.3
 */
class ApplicationRealmTest {

    ApplicationRealm realm

    @Before
    void setUp() {
        realm = new ApplicationRealm()
    }

    @Test(expected = IllegalStateException)
    void testInitWithoutClient() {
        realm.init() //needs client and applicationUrl properties
    }

    @Test(expected = IllegalStateException)
    void testInitWithoutApplicationUrl() {
        def client = createStrictMock(Client)

        replay client

        realm.client = client
        try {
            realm.init()
        } finally {
            verify client
        }
    }

    @Test
    void testWorkingInit() {

        def client = createStrictMock(Client)
        def href = 'https://api.stormpath.com/v1/applications/foo'

        replay client

        realm.client = client
        realm.applicationRestUrl = href

        realm.init()

        verify client
    }

    @Test
    void testSetClient() {
        def client = createStrictMock(Client)

        replay client

        realm.client = client

        assertSame client, realm.client

        verify client
    }

    @Test
    void testDoGetAuthenticationInfoSuccess() {

        def appHref = 'https://api.stormpath.com/v1/applications/foo'
        def username = 'jsmith'
        def password = 'secret'
        def host = '123.456.789.012'
        def acctHref = 'https://api.stormpath.com/v1/accounts/123'
        def email = 'jsmith@foo.com'
        def acctGivenName = 'John'
        def acctMiddleName = 'A'
        def acctSurname = 'Smith'


        def client = createStrictMock(Client)
        def ds = createStrictMock(DataStore)
        def app = createStrictMock(Application)
        def authcResult = createStrictMock(AuthenticationResult)
        def account = createStrictMock(Account)

        expect(client.dataStore).andStubReturn(ds)
        expect(ds.getResource(eq(appHref), same(Application))).andReturn(app)
        expect(app.authenticateAccount(anyObject() as AuthenticationRequest)).andAnswer( new IAnswer<AuthenticationResult>() {
            AuthenticationResult answer() throws Throwable {
                def authcRequest = getCurrentArguments()[0] as AuthenticationRequest

                assertEquals username, authcRequest.principals
                assertTrue Arrays.equals(password.toCharArray(), authcRequest.credentials as char[])
                assertEquals host, authcRequest.host

                return authcResult
            }
        })
        expect(authcResult.account).andReturn account

        expect(account.href).andStubReturn(acctHref)
        expect(account.username).andReturn(username)
        expect(account.email).andReturn(email)
        expect(account.givenName).andReturn(acctGivenName)
        expect(account.middleName).andReturn(acctMiddleName)
        expect(account.surname).andReturn(acctSurname)

        replay client, ds, app, authcResult, account

        realm.client = client
        realm.applicationRestUrl = appHref

        def upToken = new UsernamePasswordToken(username, password, host)
        def info = realm.doGetAuthenticationInfo(upToken)

        assertTrue info instanceof SimpleAuthenticationInfo
        assertEquals 2, info.principals.asSet().size()

        assertEquals acctHref, info.principals.iterator().next()
        assertEquals acctHref, info.principals.primaryPrincipal

        def m = info.principals.oneByType(Map)
        assertNotNull m
        assertEquals 6, m.size()
        assertEquals acctHref, m.href
        assertEquals username, m.username
        assertEquals email, m.email
        assertEquals acctGivenName, m.givenName
        assertEquals acctMiddleName, m.middleName
        assertEquals acctSurname, m.surname

        verify client, ds, app, authcResult, account
    }

    @Test(expected=AuthenticationException)
    void testDoGetAuthenticationInfoResourceException() {

        def appHref = 'https://api.stormpath.com/v1/applications/foo'
        def client = createStrictMock(Client)
        def ds = createStrictMock(DataStore)
        def app = createStrictMock(Application)

        int status = 400
        int code = 400
        def msg = 'Invalid username or password.'
        def devMsg = 'Invalid username or password.'
        def moreInfo = 'mailto:support@stormpath.com'

        expect(client.dataStore).andStubReturn(ds)

        def error = new SimpleError(status:status, code:code, message: msg, developerMessage: devMsg, moreInfo: moreInfo)

        expect(ds.getResource(eq(appHref), same(Application))).andReturn app
        expect(app.authenticateAccount(anyObject() as AuthenticationRequest)).andThrow(new ResourceException(error))

        replay client, ds, app

        realm.client = client
        realm.applicationRestUrl = appHref

        def upToken = new UsernamePasswordToken('foo', 'bar', 'baz')
        try {
            realm.doGetAuthenticationInfo(upToken)
        }
        finally {
            verify client, ds, app
        }
    }
}
