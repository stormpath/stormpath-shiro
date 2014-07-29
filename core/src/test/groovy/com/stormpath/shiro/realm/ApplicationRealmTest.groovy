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
package com.stormpath.shiro.realm

import com.stormpath.sdk.account.Account
import com.stormpath.sdk.application.Application
import com.stormpath.sdk.authc.AuthenticationRequest
import com.stormpath.sdk.authc.AuthenticationResult
import com.stormpath.sdk.client.Client
import com.stormpath.sdk.ds.DataStore
import com.stormpath.sdk.lang.Objects
import com.stormpath.sdk.provider.FacebookProviderData
import com.stormpath.sdk.provider.ProviderAccountRequest
import com.stormpath.sdk.provider.ProviderAccountResult
import com.stormpath.sdk.resource.ResourceException
import com.stormpath.shiro.authc.FacebookAuthenticationToken
import com.stormpath.shiro.authc.GoogleAuthenticationToken
import org.apache.shiro.authc.AuthenticationException
import org.apache.shiro.authc.AuthenticationInfo
import org.apache.shiro.authc.SimpleAuthenticationInfo
import org.apache.shiro.authc.UsernamePasswordToken
import org.apache.shiro.authc.credential.AllowAllCredentialsMatcher
import org.apache.shiro.cache.Cache
import org.apache.shiro.cache.MemoryConstrainedCacheManager
import org.apache.shiro.mgt.DefaultSecurityManager
import org.apache.shiro.subject.PrincipalCollection
import org.apache.shiro.subject.SimplePrincipalCollection
import org.apache.shiro.subject.Subject
import org.easymock.IAnswer
import org.easymock.IArgumentMatcher
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

    @Test
    void testDefaultInstance() {
        assertTrue realm.getCredentialsMatcher() instanceof AllowAllCredentialsMatcher //allow all - Stormpath will do the credentials comparison as necessary
        assertTrue realm.getGroupRoleResolver() instanceof DefaultGroupRoleResolver
        assertTrue realm.getGroupPermissionResolver() instanceof GroupCustomDataPermissionResolver
        assertTrue realm.getAccountPermissionResolver() instanceof AccountCustomDataPermissionResolver
        assertNull realm.getAccountRoleResolver()
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
    void testSetAccountRoleResolver() {
        def r = createStrictMock(AccountRoleResolver)

        replay r
        realm.accountRoleResolver = r

        assertSame r, realm.getAccountRoleResolver()

        verify r
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

    // @since 0.7.0
    @Test
    void testDoGetAuthenticationInfoSuccessWithOauthToken() {

        def appHref = 'https://api.stormpath.com/v1/applications/foo'
        def username = 'jsmith'
        def acctHref = 'https://api.stormpath.com/v1/accounts/123'
        def email = 'jsmith@foo.com'
        def acctGivenName = 'John'
        def acctMiddleName = 'A'
        def acctSurname = 'Smith'

        def facebookCode = "someFooGoogleCode"

        def client = createStrictMock(Client)
        def ds = createStrictMock(DataStore)
        def app = createStrictMock(Application)
        def authcResult = createStrictMock(ProviderAccountResult)
        def account = createStrictMock(Account)

        expect(client.dataStore).andStubReturn(ds)
        expect(ds.getResource(eq(appHref), same(Application))).andReturn(app)
        expect(app.getAccount(anyObject() as ProviderAccountRequest)).andAnswer( new IAnswer<ProviderAccountResult>() {
            ProviderAccountResult answer() throws Throwable {
                def authcRequest = getCurrentArguments()[0] as ProviderAccountRequest

                assertEquals facebookCode, ((FacebookProviderData)authcRequest.providerData).accessToken
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

        def upToken = new FacebookAuthenticationToken(facebookCode)
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

    /**
     * @since 0.6.0
     */
    @Test
    void testGetAuthenticationCacheKeyWithEmail() {

        def appHref = 'https://api.stormpath.com/v1/applications/foo'
        def accountHref = 'https://api.stormpath.com/v1/accounts/3107eAtpiK67G6eTI0GrJo'
        def username = 'jsmith'
        def email = 'jsmith@foo.com'
        def acctGivenName = 'John'
        def acctMiddleName = 'A'
        def acctSurname = 'Smith'

        def client = createStrictMock(Client)
        def dataStore = createStrictMock(DataStore)
        def app = createStrictMock(Application)
        def authcResult = createStrictMock(AuthenticationResult)
        def account = createStrictMock(Account)
        def cacheManager = createStrictMock(MemoryConstrainedCacheManager)
        def authcCache = createStrictMock(Cache)
        def authzCache = createStrictMock(Cache)
        def authenticationInfoEquals = new AuthenticationInfoEquals()

        expect(cacheManager.getCache(contains("com.stormpath.shiro.realm.ApplicationRealm.authenticationCache"))).andReturn(authcCache)
        expect(cacheManager.getCache(contains("com.stormpath.shiro.realm.ApplicationRealm.authorizationCache"))).andReturn(authzCache)
        expect(dataStore.getResource(appHref, Application)).andStubReturn(app)
        expect(app.authenticateAccount(anyObject() as AuthenticationRequest)).andReturn(authcResult)
        expect(client.getDataStore()).andReturn(dataStore)
        expect(authcResult.getAccount()).andReturn(account)
        expect(account.href).andReturn(accountHref)
        expect(account.username).andReturn(username)
        expect(account.email).andReturn(email)
        expect(account.givenName).andReturn(acctGivenName)
        expect(account.middleName).andReturn(acctMiddleName)
        expect(account.surname).andReturn(acctSurname)
        expect(account.href).andReturn(accountHref)
        expect(authcCache.get(email)).andReturn(authcResult)
        expect(authcCache.remove(email)).andReturn(null)
        expect(authzCache.remove((AuthenticationInfo) reportMatcher(authenticationInfoEquals))).andReturn(null)

        replay client, dataStore, app, authcResult, account, cacheManager, authcCache, authzCache

        def realm = new ApplicationRealm()
        realm.client = client
        realm.applicationRestUrl = appHref
        realm.authenticationCachingEnabled = true

        def securityManager = new DefaultSecurityManager(realm)
        securityManager.cacheManager = cacheManager

        def upToken = new UsernamePasswordToken(email, 'bar', 'baz')
        def returnedAuthenticationInfo = realm.doGetAuthenticationInfo(upToken)
        authenticationInfoEquals.setAuthenticationInfo(returnedAuthenticationInfo)
        Subject subject = new Subject.Builder(securityManager).principals(returnedAuthenticationInfo.principals).buildSubject()
        securityManager.logout(subject)

        verify client, dataStore, app, authcResult, account, cacheManager, authcCache, authzCache
    }


    /**
     * @since 0.6.0
     */
    @Test
    void testGetAuthenticationCacheKeyWithUsername() {

        def appHref = 'https://api.stormpath.com/v1/applications/foo'
        def accountHref = 'https://api.stormpath.com/v1/accounts/3107eAtpiK67G6eTI0GrJo'
        def username = 'jsmith'
        def email = 'jsmith@foo.com'
        def acctGivenName = 'John'
        def acctMiddleName = 'A'
        def acctSurname = 'Smith'

        def client = createStrictMock(Client)
        def dataStore = createStrictMock(DataStore)
        def app = createStrictMock(Application)
        def authcResult = createStrictMock(AuthenticationResult)
        def account = createStrictMock(Account)
        def cacheManager = createStrictMock(MemoryConstrainedCacheManager)
        def authcCache = createStrictMock(Cache)
        def authzCache = createStrictMock(Cache)
        def authenticationInfoEquals = new AuthenticationInfoEquals()

        expect(cacheManager.getCache(contains("com.stormpath.shiro.realm.ApplicationRealm.authenticationCache"))).andReturn(authcCache)
        expect(cacheManager.getCache(contains("com.stormpath.shiro.realm.ApplicationRealm.authorizationCache"))).andReturn(authzCache)
        expect(dataStore.getResource(appHref, Application)).andStubReturn(app)
        expect(app.authenticateAccount(anyObject() as AuthenticationRequest)).andReturn(authcResult)
        expect(client.getDataStore()).andReturn(dataStore)
        expect(authcResult.getAccount()).andReturn(account)
        expect(account.href).andReturn(accountHref)
        expect(account.username).andReturn(username)
        expect(account.email).andReturn(email)
        expect(account.givenName).andReturn(acctGivenName)
        expect(account.middleName).andReturn(acctMiddleName)
        expect(account.surname).andReturn(acctSurname)
        expect(account.href).andReturn(accountHref)
        expect(authcCache.get(email)).andReturn(null)
        expect(authcCache.remove(username)).andReturn(null)
        expect(authzCache.remove((AuthenticationInfo) reportMatcher(authenticationInfoEquals))).andReturn(null)

        replay client, dataStore, app, authcResult, account, cacheManager, authcCache, authzCache

        def realm = new ApplicationRealm()
        realm.client = client
        realm.applicationRestUrl = appHref
        realm.authenticationCachingEnabled = true

        def securityManager = new DefaultSecurityManager(realm)
        securityManager.cacheManager = cacheManager

        def upToken = new UsernamePasswordToken(username, 'bar', 'baz')
        def returnedAuthenticationInfo = realm.doGetAuthenticationInfo(upToken)
        authenticationInfoEquals.setAuthenticationInfo(returnedAuthenticationInfo)
        Subject subject = new Subject.Builder(securityManager).principals(returnedAuthenticationInfo.principals).buildSubject()
        securityManager.logout(subject)

        verify client, dataStore, app, authcResult, account, cacheManager, authcCache, authzCache
    }

    /**
     * @since 0.6.0
     */
    @Test
    void testGetAuthenticationCacheKeyNullPrincipals() {
        assertNull(realm.getAuthenticationCacheKey((PrincipalCollection) null))
    }

    /**
     * @since 0.6.0
     */
    @Test
    void testGetAuthenticationCacheKeyEmptyPrincipals() {
        def principals  = createStrictMock(PrincipalCollection)
        def primaryPrincipal  = createStrictMock(Object)

        expect(principals.isEmpty()).andReturn(false)
        expect(principals.fromRealm(contains("com.stormpath.shiro.realm.ApplicationRealm"))).andReturn(null)
        expect(principals.getPrimaryPrincipal()).andReturn(primaryPrincipal)

        replay principals, primaryPrincipal

        assertSame(realm.getAuthenticationCacheKey(principals), primaryPrincipal)

        verify principals, primaryPrincipal
    }

    /**
     * @since 0.6.0
     */
    static class AuthenticationInfoEquals implements IArgumentMatcher {

        private AuthenticationInfo expected

        public setAuthenticationInfo(AuthenticationInfo authenticationInfo) {
            expected = authenticationInfo;
        }

        boolean matches(Object o) {
            if (o == null || ! SimplePrincipalCollection.isInstance(o)) {
                return false;
            }
            SimplePrincipalCollection actual = (SimplePrincipalCollection) o
            return (Objects.nullSafeEquals(expected.principals, actual))
        }

        void appendTo(StringBuffer stringBuffer) {
            stringBuffer.append(expected.toString())
        }
    }
}
