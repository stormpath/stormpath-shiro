package com.stormpath.shiro.realm

import com.stormpath.sdk.directory.CustomData
import org.apache.shiro.authz.permission.PermissionResolver
import org.testng.annotations.Test

import static org.easymock.EasyMock.createStrictMock
import static org.testng.Assert.*

class CustomDataPermissionResolverTest {

    @Test
    void testCustomDataFieldName() {
        def resolver = new CustomDataPermissionResolver()
        resolver.setCustomDataFieldName('foo')
        assertEquals 'foo', resolver.getCustomDataFieldName()
    }

    @Test
    void testSetPermissionResolver() {
        def permResolver = createStrictMock(PermissionResolver)
        def resolver = new CustomDataPermissionResolver()
        resolver.setPermissionResolver(permResolver)
        assertSame permResolver, resolver.getPermissionResolver()
    }

    @Test
    void testGetPermissionsWithNoCustomData() {

        def customData = createStrictMock(CustomData)

        def resolver = new CustomDataPermissionResolver() {
            @Override
            protected Set<String> getPermissionStrings(CustomData cd) {
                return null
            }
        }

        def set = resolver.getPermissions(customData)

        assertNotNull set
        assertTrue set.isEmpty()
    }
}
