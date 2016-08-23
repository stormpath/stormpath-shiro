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
package com.stormpath.shiro.authz;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.directory.CustomData;
import com.stormpath.sdk.group.Group;
import com.stormpath.sdk.lang.Assert;
import org.apache.shiro.util.CollectionUtils;
import org.apache.shiro.util.StringUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * A {@code CustomDataPermissionsEditor} can read or modify a collection of Shiro permission Strings stored in a
 * {@link CustomData} resource.  This is used to support a common convention for Shiro+Stormpath applications: you can
 * assign Shiro permissions to an {@link Account} or a {@link Group} by storing those permissions in the respective
 * account or group's {@link CustomData} map.
 * <h3>Usage</h3>
 * You may use this component to 'wrap' a CustomData instance, and it will read or modify the CustomData's permissions
 * Set as necessary.  This implementation assumes a single CustomData field that contains a Set of Strings.
 * <p/>
 * For example:
 * <pre>
 *     CustomData data = account.getCustomData();
 *     new CustomDataPermissionsEditor(customData)
 *         .append("user:1234:edit")
 *         .append("document:*")
 *         .remove("printer:*:print");
 *     data.save();
 * </pre>
 * Invoking this code would remove the first two permissions and remove the 3rd.
 * <p/>
 * <b>Note however that manipulating
 * the permissions Set only makes changes locally.  You must call
 * {@code customData.}{@link com.stormpath.sdk.directory.CustomData#save() save()} (or account.save() or group.save())
 * to persist the the changes back to Stormpath.</b>
 *
 * <h3>Field Name</h3>
 * This implementation assumes a CustomData field named {@code apacheShiroPermissions} to store the Set of shiro
 * permissions, implying the following CustomData JSON structure:
 * <pre>
 * {
 *     ... any other of your own custom data properties ...,
 *
 *     "apacheShiroPermissions": [
 *         "perm1",
 *         "perm2",
 *         ...,
 *         "permN"
 *     ]
 * }
 * </pre>
 * You can set the {@link #setFieldName(String) fieldName} property if you would like to change this name to something
 * else.  For example, if you changed the name to {@code myApplicationPermissions}, you would see the resulting
 * CustomData JSON structure:
 * <pre>
 * {
 *     ... any other of your own custom data properties ...,
 *
 *     "myApplicationPermissions": [
 *         "perm1",
 *         "perm2",
 *         ...,
 *         "permN"j
 *     ]
 * }
 * </pre>
 *
 * @since 0.5
 */
public class CustomDataPermissionsEditor implements PermissionsEditor {

    public static final String DEFAULT_CUSTOM_DATA_FIELD_NAME = "apacheShiroPermissions";

    private final CustomData CUSTOM_DATA;

    private String fieldName = DEFAULT_CUSTOM_DATA_FIELD_NAME;

    /**
     * Creates a new CustomDataPermissionsEditor that will delegate to the specified {@link CustomData} instance.
     *
     * @param customData the CustomData instance that may store a Set of shiro permission strings.
     */
    public CustomDataPermissionsEditor(CustomData customData) {
        Assert.notNull(customData, "CustomData argument cannot be null.");
        this.CUSTOM_DATA = customData;
    }

    /**
     * Returns the name of the {@link CustomData} field used to store the {@code Set&lt;String&gt;}
     * of permissions.  The default name is
     * {@code apacheShiroPermissions}, implying a {@code CustomData} JSON representation as follows:
     * <pre>
     * {
     *     ... any other of your own custom data properties ...,
     *
     *     "apacheShiroPermissions": [
     *         "perm1",
     *         "perm2",
     *         ...,
     *         "permN"j
     *     ]
     * }
     * </pre>
     * You can change the name by calling {@link #setFieldName(String)}.
     *
     * @return the name of the {@link CustomData} field used to store the {@code Set&lt;String&gt;}
     *         of permissions.
     */
    public String getFieldName() {
        return this.fieldName;
    }

    /**
     * Sets the name of the {@link CustomData} field used to store the {@code Set&lt;String&gt;}
     * of permissions.  The default name is
     * {@code apacheShiroPermissions}, implying a {@code CustomData} JSON representation as follows:
     * <pre>
     * {
     *     ... any other of your own custom data properties ...,
     *
     *     "apacheShiroPermissions": [
     *         "perm1",
     *         "perm2",
     *         ...,
     *         "permN"j
     *     ]
     * }
     * </pre>
     * If you changed this name to be {@code myApplicationPermissions} for example, the CustomData representation
     * would look something like this instead:
     * <pre>
     * {
     *     ... any other of your own custom data properties ...,
     *
     *     "myApplicationPermissions": [
     *         "perm1",
     *         "perm2",
     *         ...,
     *         "permN"j
     *     ]
     * }
     * </pre>
     * <h3>Usage Warning</h3>
     * If you change this value, you will also need to adjust your {@code ApplicationRealm} instance's configuration
     * to reflect this name so it can continue to function - the realm reads the same {@code CustomData} field, so
     * they must be identical to ensure both read and write scenarios access the same field.
     * <p/>
     * For example, if using {@code shiro.ini}:
     * <pre>
     * stormpathRealm.groupPermissionResolver.customDataFieldName = myApplicationPermissions
     * stormpathRealm.accountPermissionResolver.customDataFieldName = myApplicationPermissions
     * </pre>
     *
     * @param fieldName the name of the {@link CustomData} field used to store the {@code Set&lt;String&gt;}
     *                  of permissions.
     * @return this object for method chaining.
     */
    public CustomDataPermissionsEditor setFieldName(String fieldName) {
        this.fieldName = fieldName;
        return this;
    }

    @Override
    public PermissionsEditor append(String perm) {
        Assert.hasText(perm, "Permission string argument cannot be null or empty.");

        Collection<String> perms = lookupPermissionStrings();

        String fieldName = getFieldName();

        if (perms == null) {
            perms = new LinkedHashSet<String>();
            CUSTOM_DATA.put(fieldName, perms);
        } else if (perms instanceof List) {
            //hasn't yet been converted to a set that we maintain:
            perms = asSet(fieldName, (List) perms);
            CUSTOM_DATA.put(fieldName, perms);
        }
        //else the Collection should be a Set

        perms.add(perm);

        return this;
    }

    @Override
    public PermissionsEditor remove(String perm) {
        if (StringUtils.hasText(perm)) {
            Collection<String> perms = lookupPermissionStrings();
            if (!CollectionUtils.isEmpty(perms)) {
                if (perms instanceof List) {
                    //hasn't yet been converted to a set that we maintain:
                    String attrName = getFieldName();
                    perms = asSet(attrName, (List) perms);
                    CUSTOM_DATA.put(attrName, perms);
                }
                perms.remove(perm);
            }
        }
        return this;
    }

    @Override
    public Set<String> getPermissionStrings() {

        Collection<String> perms = lookupPermissionStrings();

        if (CollectionUtils.isEmpty(perms)) {
            return Collections.emptySet();
        }

        Set<String> set;

        if (perms instanceof List) {
            set = asSet(getFieldName(), (List) perms);
        } else {
            assert perms instanceof Set : "perms instance must be a Set<String>";
            set = (Set<String>) perms;
        }

        return Collections.unmodifiableSet(set);
    }

    private static Set<String> asSet(String fieldName, List list) {
        Set<String> set = new LinkedHashSet<String>(list.size());
        for (Object element : list) {
            if (element != null) {
                if (!(element instanceof String)) {
                    String msg = "CustomData field '" + fieldName + "' contains an element that is not a String " +
                            "as required. Element type: " + element.getClass().getName() + ", element value: " + element;
                    throw new IllegalArgumentException(msg);
                }
                String s = (String) element;

                set.add(s);
            }
        }
        return set;
    }

    @SuppressWarnings("unchecked")
    private Collection<String> lookupPermissionStrings() {

        final String fieldName = getFieldName();

        Object value = CUSTOM_DATA.get(fieldName);

        if (value == null) {
            return null;
        }

        if (value instanceof Set) {
            return (Set<String>) value;
        }

        List permList = null;

        if (value instanceof List) {
            permList = (List) value;
        } else {
            String msg = "Unable to recognize CustomData field '" + fieldName + "' value of type " +
                    value.getClass().getName() + ".  Expected type: Set<String> or List<String>.";
            throw new IllegalArgumentException(msg);
        }

        return permList;
    }

}
