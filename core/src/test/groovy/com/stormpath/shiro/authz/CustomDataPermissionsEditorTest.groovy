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
package com.stormpath.shiro.authz

import org.codehaus.jackson.map.ObjectMapper
import org.junit.Test

import static org.junit.Assert.*

class CustomDataPermissionsEditorTest {

    @Test
    void testConstantValue() {
        //This ensures we don't change the constant value - doing so would not be runtime backwards compatible.
        //If the value is changed in code, this test will fail, as expected (DO NOT change the value!)
        assertEquals "apacheShiroPermissions", CustomDataPermissionsEditor.DEFAULT_CUSTOM_DATA_FIELD_NAME
    }

    @Test(expected = IllegalArgumentException)
    void testNewInstanceWithNullArg() {
        new CustomDataPermissionsEditor(null)
    }

    @Test
    void tesNewInstance() {
        def customData = new MockCustomData()
        def editor = new CustomDataPermissionsEditor(customData)
        assertSame customData, editor.CUSTOM_DATA
        assertEquals CustomDataPermissionsEditor.DEFAULT_CUSTOM_DATA_FIELD_NAME, editor.getFieldName()
    }

    @Test
    void testSetFieldName() {
        def customData = new MockCustomData()
        def editor = new CustomDataPermissionsEditor(customData)
        def fieldName = 'foo'
        editor.setFieldName(fieldName)
        assertEquals fieldName, editor.getFieldName()

        editor.append('bar')

        //assert changed
        assertFalse customData.containsKey(CustomDataPermissionsEditor.DEFAULT_CUSTOM_DATA_FIELD_NAME)
        assertTrue customData.containsKey('foo')
        assertEquals 'bar', editor.getPermissionStrings().iterator().next()
    }

    @Test //tests that we can append a value even if there is not yet a field dedicated for storing perms
    void testAppendWhenNonExistent() {
        def customData = new MockCustomData()
        def editor = new CustomDataPermissionsEditor(customData)

        assertNull customData.apacheShiroPermissions

        editor.append('foo:*')

        assertNotNull customData.apacheShiroPermissions
        assertTrue customData.apacheShiroPermissions instanceof LinkedHashSet
        assertEquals 1, customData.apacheShiroPermissions.size()
        assertEquals 'foo:*', customData.apacheShiroPermissions.iterator().next()
    }

    @Test //tests that we can append a value when there is already a perm collection present
    void testAppendWithExistingList() {
        def customData = new MockCustomData()
        customData.apacheShiroPermissions = ['foo:*']

        def editor = new CustomDataPermissionsEditor(customData)

        assertNotNull customData.apacheShiroPermissions
        assertTrue customData.apacheShiroPermissions instanceof List

        editor.append('bar:*')

        assertNotNull customData.apacheShiroPermissions
        assertTrue customData.apacheShiroPermissions instanceof LinkedHashSet
        assertEquals 2, customData.apacheShiroPermissions.size()
        def i = customData.apacheShiroPermissions.iterator()

        assertEquals 'foo:*', i.next()
        assertEquals 'bar:*', i.next()
    }

    @Test
    void testRemoveWithNullArg() {

        def customData = new MockCustomData()
        def editor = new CustomDataPermissionsEditor(customData)

        assertNull customData.apacheShiroPermissions

        editor.remove(null)

        assertNull customData.apacheShiroPermissions
    }

    @Test
    void testRemoveWithEmptyArg() {

        def customData = new MockCustomData()
        def editor = new CustomDataPermissionsEditor(customData)

        assertNull customData.apacheShiroPermissions

        editor.remove('   ')

        assertNull customData.apacheShiroPermissions
    }

    @Test
    void testRemoveWhenNonExistent() {

        def customData = new MockCustomData()
        def editor = new CustomDataPermissionsEditor(customData)

        assertNull customData.apacheShiroPermissions

        editor.remove('foo')

        assertNull customData.apacheShiroPermissions
    }

    @Test
    void testRemoveWithExistingList() {
        def customData = new MockCustomData()
        customData.apacheShiroPermissions = ['foo:*', 'bar']
        def editor = new CustomDataPermissionsEditor(customData)

        def result = editor.getPermissionStrings()

        assertNotNull result
        assertEquals 2, result.size()
        def i = result.iterator()
        assertEquals 'foo:*', i.next()
        assertEquals 'bar', i.next()

        editor.remove('foo:*')

        result = editor.getPermissionStrings()
        assertNotNull result
        assertEquals 1, result.size()
        assertEquals 'bar', result.iterator().next()
    }

    @Test
    void testRemoveWithExistingSet() {
        def customData = new MockCustomData()
        def editor = new CustomDataPermissionsEditor(customData)

        editor.append('foo:*').append('bar')

        def result = editor.getPermissionStrings()

        assertNotNull result
        assertEquals 2, result.size()
        def i = result.iterator()
        assertEquals 'foo:*', i.next()
        assertEquals 'bar', i.next()

        editor.remove('foo:*')

        result = editor.getPermissionStrings()
        assertNotNull result
        assertEquals 1, result.size()
        assertEquals 'bar', result.iterator().next()
    }

    @Test
    void testGetPermissionStringsWhenNonExistent() {

        def customData = new MockCustomData()
        def editor = new CustomDataPermissionsEditor(customData)

        assertNull customData.apacheShiroPermissions

        def result = editor.getPermissionStrings();

        assertNotNull result
        assertTrue result.isEmpty()
    }

    @Test(expected=UnsupportedOperationException)
    void testGetPermissionStringsReturnsImmutableSet() {

        def customData = new MockCustomData()
        def editor = new CustomDataPermissionsEditor(customData)

        assertNull customData.apacheShiroPermissions

        Set result = editor.getPermissionStrings();

        assertNotNull result
        assertTrue result.isEmpty()

        result.add('foo')
    }

    @Test
    void testGetPermissionStringsWithExistingList() {

        def customData = new MockCustomData()
        customData.apacheShiroPermissions = ['foo:*']
        def editor = new CustomDataPermissionsEditor(customData)

        def result = editor.getPermissionStrings();

        assertNotNull result
        assertEquals 1, result.size()
        assertEquals 'foo:*', result.iterator().next()
    }

    @Test
    void testGetPermissionStringsWithExistingValuesSomeWithNull() {

        def customData = new MockCustomData()
        customData.apacheShiroPermissions = ['foo:*', null, 'bar']
        def editor = new CustomDataPermissionsEditor(customData)

        def result = editor.getPermissionStrings();

        assertNotNull result
        assertEquals 2, result.size()
        def i = result.iterator()
        assertEquals 'foo:*', i.next()
        assertEquals 'bar', i.next()
    }

    @Test
    void testGetPermissionStringsWithExistingValuesSomeNotStrings() {

        def customData = new MockCustomData()
        def nonString = 123
        customData.apacheShiroPermissions = ['foo:*', nonString, 'bar'] //tests user erroneous population
        def editor = new CustomDataPermissionsEditor(customData)

        try {
            editor.getPermissionStrings();
        } catch (IllegalArgumentException iae) {
            String expectedMsg = "CustomData field 'apacheShiroPermissions' contains an element that is not a String as " +
                    "required. Element type: " + nonString.getClass().getName() + ", element value: " + nonString
            assertEquals expectedMsg, iae.getMessage()
        }
    }

    @Test
    void testGetPermissionStringsWithExistingStringArray() {

        ObjectMapper mapper = new ObjectMapper()
        def json = '''
        {
            "apacheShiroPermissions": [
                "foo:*",
                "bar"
            ]
        }
        '''
        def m = mapper.readValue(json, Map.class)

        def customData = new MockCustomData()
        customData.apacheShiroPermissions = m.apacheShiroPermissions
        def editor = new CustomDataPermissionsEditor(customData)

        def result = editor.getPermissionStrings();

        assertNotNull result
        assertEquals 2, result.size()
        def i = result.iterator()
        assertEquals 'foo:*', i.next()
        assertEquals 'bar', i.next()
    }

    @Test
    void testGetPermissionStringsWithNonListProperty() {

        def value = 42
        def customData = new MockCustomData()
        customData.apacheShiroPermissions = value
        def editor = new CustomDataPermissionsEditor(customData)

        try {
            editor.getPermissionStrings();
        } catch (IllegalArgumentException iae) {
            String expectedMsg = "Unable to recognize CustomData field 'apacheShiroPermissions' value of type " +
                    value.getClass().getName() + ".  Expected type: Set<String> or List<String>."
            assertEquals expectedMsg, iae.getMessage();
        }
    }

}
