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

import com.stormpath.sdk.group.Group
import org.testng.annotations.BeforeTest
import org.testng.annotations.Test

import static org.easymock.EasyMock.*
import static org.testng.Assert.*

/**
 * @since 0.3
 */
class DefaultGroupRoleResolverTest {

    DefaultGroupRoleResolver resolver

    @BeforeTest
    void setUp() {
        resolver = new DefaultGroupRoleResolver()
    }

    @Test
    void testDefaultInstance() {
        assertEquals 1, resolver.modes.size()
        assertSame DefaultGroupRoleResolver.Mode.HREF, resolver.modes.iterator().next()
        def modeNames = resolver.modeNames
        assertEquals 1, modeNames.size()
        assertEquals DefaultGroupRoleResolver.Mode.HREF.name(), modeNames.iterator().next()
    }

    @Test
    void testSetModes() {
        resolver.setModes([DefaultGroupRoleResolver.Mode.ID] as Set)
        assertEquals 1, resolver.modes.size()
        assertSame DefaultGroupRoleResolver.Mode.ID, resolver.modes.iterator().next()
    }

    @Test(expectedExceptions=IllegalArgumentException)
    void testSetNullModes() {
        resolver.setModes(null)
    }

    @Test(expectedExceptions=IllegalArgumentException)
    void testSetEmptyModes() {
        resolver.setModes(Collections.emptySet())
    }

    @Test
    void testSetModeNames() {
        resolver.setModeNames([DefaultGroupRoleResolver.Mode.ID.name()] as Set)
        assertEquals 1, resolver.modes.size()
        assertSame DefaultGroupRoleResolver.Mode.ID, resolver.modes.iterator().next()
    }

    @Test
    void testSetModeNamesLowerCase() {
        resolver.setModeNames([DefaultGroupRoleResolver.Mode.ID.name().toLowerCase()] as Set)
        assertEquals 1, resolver.modes.size()
        assertSame DefaultGroupRoleResolver.Mode.ID, resolver.modes.iterator().next()
    }

    @Test(expectedExceptions=IllegalArgumentException)
    void testSetNullModeNames() {
        resolver.setModeNames(null)
    }

    @Test(expectedExceptions=IllegalArgumentException)
    void testSetEmptyModeNames() {
        resolver.setModeNames(Collections.emptySet())
    }

    @Test(expectedExceptions=IllegalArgumentException)
    void testSetInvalidModeName() {
        resolver.setModeNames(['foo'] as Set)
    }

    @Test
    void testResolveRolesWithHref() {

        def group = createStrictMock(Group)

        def href = 'https://api.stormpath.com/groups/foo'

        expect(group.href).andReturn(href)

        replay group

        def roleNames = resolver.resolveRoles(group)

        assertEquals 1, roleNames.size()
        assertEquals href, roleNames.iterator().next()

        verify group
    }

    @Test(expectedExceptions=IllegalStateException)
    void testResolveRolesWithMissingHref() {

        def group = createStrictMock(Group)

        expect(group.href).andReturn null

        replay group

        try {
            resolver.resolveRoles(group)
        } finally {
            verify group
        }
    }

    @Test
    void testResolveRolesWithId() {

        def group = createStrictMock(Group)

        def href = 'https://api.stormpath.com/groups/foo'

        expect(group.href).andReturn(href)

        replay group

        resolver.modes = [DefaultGroupRoleResolver.Mode.ID] as Set
        def roleNames = resolver.resolveRoles(group)

        assertEquals 1, roleNames.size()
        assertEquals 'foo', roleNames.iterator().next()

        verify group
    }

    @Test
    void testResolveRolesWithIdAndInvalidHref() {

        def group = createStrictMock(Group)

        def href = 'whatever'

        expect(group.href).andReturn(href)

        replay group

        resolver.modes = [DefaultGroupRoleResolver.Mode.ID] as Set
        def roleNames = resolver.resolveRoles(group)

        assertNotNull roleNames
        assertTrue roleNames.isEmpty()

        verify group
    }

    @Test
    void testResolveRolesWithName() {

        def group = createStrictMock(Group)

        def href = 'https://api.stormpath.com/groups/foo'

        expect(group.href).andReturn(href)
        expect(group.name).andReturn('bar')

        replay group

        resolver.modes = [DefaultGroupRoleResolver.Mode.NAME] as Set
        def roleNames = resolver.resolveRoles(group)

        assertEquals 1, roleNames.size()
        assertEquals 'bar', roleNames.iterator().next()

        verify group
    }
}
