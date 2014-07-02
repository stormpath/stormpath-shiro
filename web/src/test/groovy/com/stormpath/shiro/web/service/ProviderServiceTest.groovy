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

import com.stormpath.sdk.application.AccountStoreMapping
import com.stormpath.sdk.application.AccountStoreMappingList
import com.stormpath.sdk.application.Application
import com.stormpath.sdk.client.Client
import com.stormpath.sdk.directory.Directory
import com.stormpath.sdk.provider.CreateProviderRequest
import com.stormpath.sdk.provider.Provider
import com.stormpath.sdk.tenant.Tenant
import com.stormpath.shiro.realm.ApplicationRealm
import org.apache.shiro.mgt.RealmSecurityManager
import org.apache.shiro.util.ThreadContext
import org.junit.Test

import java.lang.reflect.Field

import static org.easymock.EasyMock.*
import static org.junit.Assert.assertFalse
import static org.junit.Assert.assertTrue;

class ProviderServiceTest {

    @Test
    public void testHasProviderBasedAccountStoreFalse() {

        def securityManager = createStrictMock(RealmSecurityManager)
        def service = createMockBuilder(ProviderService.class)
                .withConstructor("someProviderId")
                .addMockedMethod("createProviderRequest")
                .createMock();
        def collection = createStrictMock(Collection)
        def iterator = createStrictMock(Iterator)
        def realm = createStrictMock(ApplicationRealm)
        def client = createStrictMock(Client)
        def appHref = "https://api.stormpath.com/v1/applications/3TqjyZ2qp74wDM1gYo2H93"
        def app = createStrictMock(Application)
        def accountStoreMappingList = createStrictMock(AccountStoreMappingList)
        def asmlIterator = createStrictMock(Iterator)

        expect(securityManager.getRealms()).andReturn(collection)
        expect(collection.iterator()).andReturn(iterator)
        expect(iterator.next()).andReturn(realm)
        expect(realm.getClient()).andReturn(client)
        expect(realm.getApplicationRestUrl()).andReturn(appHref)
        expect(client.getResource(appHref, Application)).andReturn(app)
        expect(app.getAccountStoreMappings()).andReturn(accountStoreMappingList)
        expect(accountStoreMappingList.iterator()).andReturn(asmlIterator)
        expect(asmlIterator.hasNext()).andReturn(false)

        replay securityManager, service, collection, iterator, realm, client, app, accountStoreMappingList, asmlIterator

        ThreadContext.bind(securityManager)

        assertFalse(service.hasProviderBasedAccountStore())

        verify securityManager, service, collection, iterator, realm, client, app, accountStoreMappingList, asmlIterator

        //Since service is a singleton, let's set applicationRealm to null so other tests can start with a clean instance
        setNewValue(AbstractService, service, "applicationRealm", null)
    }

    @Test
    public void testHasProviderBasedAccountStoreTrue() {

        def securityManager = createStrictMock(RealmSecurityManager)
        def service = createMockBuilder(ProviderService.class)
                .withConstructor("someProviderId")
                .addMockedMethod("createProviderRequest")
                .createMock();

        def collection = createStrictMock(Collection)
        def iterator = createStrictMock(Iterator)
        def realm = createStrictMock(ApplicationRealm)
        def client = createStrictMock(Client)
        def appHref = "https://api.stormpath.com/v1/applications/3TqjyZ2qp74wDM1gYo2H93"
        def app = createStrictMock(Application)
        def accountStoreMappingList = createStrictMock(AccountStoreMappingList)
        def asmlIterator = createStrictMock(Iterator)
        def asm = createStrictMock(AccountStoreMapping)
        def accountStore = createStrictMock(Directory)
        def accountStoreHref = "https://api.stormpath.com/v1/directories/4WGUL3lWkYAOuOZPA3Bybp"
        def provider = createStrictMock(Provider)

        expect(securityManager.getRealms()).andReturn(collection)
        expect(collection.iterator()).andReturn(iterator)
        expect(iterator.next()).andReturn(realm)
        expect(realm.getClient()).andReturn(client)
        expect(realm.getApplicationRestUrl()).andReturn(appHref)
        expect(client.getResource(appHref, Application)).andReturn(app)
        expect(app.getAccountStoreMappings()).andReturn(accountStoreMappingList)
        expect(accountStoreMappingList.iterator()).andReturn(asmlIterator)
        expect(asmlIterator.hasNext()).andReturn(true)
        expect(asmlIterator.next()).andReturn(asm)
        expect(asm.getAccountStore()).andReturn(accountStore)
        expect(accountStore.getHref()).andReturn(accountStoreHref)
        expect(accountStore.getProvider()).andReturn(provider)
        expect(provider.getProviderId()).andReturn("someProviderId")

        replay securityManager, service, collection, iterator, realm, client, app, accountStoreMappingList, asmlIterator, asm, accountStore, provider

        ThreadContext.bind(securityManager)

        assertTrue(service.hasProviderBasedAccountStore())

        verify securityManager, service, collection, iterator, realm, client, app, accountStoreMappingList, asmlIterator, asm, accountStore, provider

        //Since service is a singleton, let's set applicationRealm to null so other tests can start with a clean instance
        setNewValue(AbstractService, service, "applicationRealm", null)
    }

    @Test
    public void testCreateProviderAccountStore() {

        def securityManager = createStrictMock(RealmSecurityManager)
        def service = createMockBuilder(ProviderService.class)
                .withConstructor("someProviderId")
                .addMockedMethod("createProviderRequest")
                .createMock();
        //Since service is a singleton, we need to set applicationRealm to null so we can start over
        setNewValue(AbstractService, service, "applicationRealm", null)
        def collection = createStrictMock(Collection)
        def iterator = createStrictMock(Iterator)
        def realm = createStrictMock(ApplicationRealm)
        def client = createStrictMock(Client)
        def app = createStrictMock(Application)
        def asm = createStrictMock(AccountStoreMapping)
        def directory = createStrictMock(Directory)
        def provider = createStrictMock(Provider)
        def request = createStrictMock(CreateProviderRequest)
        def tenant = createStrictMock(Tenant)
        def appHref = "https://api.stormpath.com/v1/applications/3TqjyZ2qp74wDM1gYo2H93"
        def appName = "myAppName"

        expect(securityManager.getRealms()).andReturn(collection)
        expect(collection.iterator()).andReturn(iterator)
        expect(iterator.next()).andReturn(realm)
        expect(realm.getClient()).andReturn(client)
        expect(realm.getApplicationRestUrl()).andReturn(appHref)
        expect(realm.getClient()).andReturn(client)
        expect(client.getResource(appHref, Application)).andReturn(app)
        expect(app.getName()).andReturn(appName)
        expect(client.instantiate(Directory)).andReturn(directory)
        expect(directory.setName(contains(appName + "-SOMEPROVIDERID")))
        expect(service.createProviderRequest()).andReturn(request)
        expect(request.getProvider()).andReturn(provider)
        expect(realm.getClient()).andReturn(client)
        expect(client.getCurrentTenant()).andReturn(tenant)
        expect(tenant.createDirectory(anyObject(CreateProviderRequest))).andReturn(directory)
        expect(realm.getClient()).andReturn(client)
        expect(client.instantiate(AccountStoreMapping)).andReturn(asm)
        expect(asm.setAccountStore(directory))
        expect(asm.setApplication(app))
        expect(asm.setListIndex(Integer.MAX_VALUE))
        expect(asm.setDefaultAccountStore(false))
        expect(asm.setDefaultGroupStore(false))
        expect(app.createAccountStoreMapping(asm)).andReturn(asm)

        replay securityManager, service, collection, iterator, realm, client, app, asm, provider, directory, request, tenant

        ThreadContext.bind(securityManager)

        service.createProviderAccountStore()

        verify securityManager, service, collection, iterator, realm, client, app, asm, provider, directory, request, tenant

        //Since service is a singleton, let's set applicationRealm to null so other tests can start with a clean instance
        setNewValue(AbstractService, service, "applicationRealm", null)
    }



    private void setNewValue(Class clazz, Object object, String fieldName, Object value){
        Field field = clazz.getDeclaredField(fieldName)
        field.setAccessible(true)
        field.set(object, value)
    }

}
