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
package com.stormpath.shiro.realm;

import com.stormpath.sdk.directory.CustomData;
import com.stormpath.shiro.authz.CustomDataPermissionsEditor;
import org.apache.shiro.authz.Permission;
import org.apache.shiro.authz.permission.PermissionResolver;
import org.apache.shiro.authz.permission.WildcardPermissionResolver;
import org.apache.shiro.util.CollectionUtils;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * A {@code CustomDataPermissionResolver} accesses a
 * {@link #getCustomDataFieldName() specific named field} value of a {@link CustomData} resource that contains
 * a {@code Set&lt;String&gt;} of Shiro permission Strings.
 * <p/>
 * The permissions stored in that field are assumed to be assigned to the CustomData's owning entity (an
 * {@link com.stormpath.sdk.account.Account Account} or {@link com.stormpath.sdk.group.Group Group}).
 * <h3>Custom Data Field Name</h3>
 * You can configure what named field is used to store the permissions via
 * {@link #setCustomDataFieldName(String) fieldName} property.
 * <h3>String to Permission Conversion</h3>
 * The Strings stored in the CustomData resource are converted to Shiro {@link Permission} instances via the
 * {@link #getPermissionResolver() permissionResolver} property.  Unless overridden, the default instance is a Shiro
 * {@link WildcardPermissionResolver}.
 *
 * @see AccountCustomDataPermissionResolver
 * @see GroupCustomDataPermissionResolver
 * @since 0.5
 */
public class CustomDataPermissionResolver {

    private String customDataFieldName;

    private PermissionResolver permissionResolver;

    /**
     * Creates a new instance, using the default {@link #getCustomDataFieldName() customDataFieldName} of
     * {@code apacheShiroPermissions} and a default {@link WildcardPermissionResolver}.
     */
    public CustomDataPermissionResolver() {
        this.customDataFieldName = CustomDataPermissionsEditor.DEFAULT_CUSTOM_DATA_FIELD_NAME;
        this.permissionResolver = new WildcardPermissionResolver();
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
     * You can change the name by calling {@link #setCustomDataFieldName(String)}.
     *
     * @return the name of the {@link CustomData} field used to store the {@code Set&lt;String&gt;}
     *         of permissions.
     */
    public String getCustomDataFieldName() {
        return customDataFieldName;
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
     *
     * @param customDataFieldName the name of the {@link CustomData} field used to store the {@code Set&lt;String&gt;}
     *                            of permissions.
     */
    public void setCustomDataFieldName(String customDataFieldName) {
        this.customDataFieldName = customDataFieldName;
    }

    /**
     * Returns the {@link PermissionResolver} used to convert {@link #getCustomDataFieldName() stored permission strings}
     * to Shiro {@link Permission} instances for {@link org.apache.shiro.subject.Subject Subject} authorization.
     * <p/>
     * The default / pre-configured instance is a {@link WildcardPermissionResolver}.
     *
     * @return the {@link PermissionResolver} used to convert {@link #getCustomDataFieldName() stored permission strings}
     *         to Shiro {@link Permission} instances for {@link org.apache.shiro.subject.Subject Subject} authorization.
     */
    public PermissionResolver getPermissionResolver() {
        return permissionResolver;
    }

    /**
     * Sets the {@link PermissionResolver} used to convert {@link #getCustomDataFieldName() stored permission strings}
     * to Shiro {@link Permission} instances for {@link org.apache.shiro.subject.Subject Subject} authorization.
     *
     * @param permissionResolver the {@link PermissionResolver} used to convert {@link #getCustomDataFieldName() stored permission strings}
     *                           to Shiro {@link Permission} instances for {@link org.apache.shiro.subject.Subject Subject} authorization.
     */
    public void setPermissionResolver(PermissionResolver permissionResolver) {
        this.permissionResolver = permissionResolver;
    }

    /**
     * Returns a {@code Set&lt;String&gt;} of permission strings that are stored in the specified
     * {@link CustomData} instance (under the {@link #getCustomDataFieldName() customDataFieldName} key), or an
     * empty collection if no permissions are stored.
     * <p/>
     * This implementation internally delegates field access and Set construction to a
     * {@link CustomDataPermissionsEditor} instance, e.g.
     * <pre>
     * return new CustomDataPermissionsEditor(customData)
     *     .setFieldName(getCustomDataFieldName())
     *     .getPermissionStrings();
     * </pre>
     *
     * @param customData the custom data instance that might have a {@code Set&lt;String&gt;} of permissions stored in
     *                   a key named {@link #getCustomDataFieldName()}.
     * @return a {@code Set&lt;String&gt;} of permission strings that are stored in the specified
     *         {@link CustomData} instance (under the {@link #getCustomDataFieldName() customDataFieldName} key), or an
     *         empty collection if no permissions are stored.
     * @see CustomDataPermissionsEditor
     */
    protected Set<String> getPermissionStrings(CustomData customData) {
        return new CustomDataPermissionsEditor(customData)
                .setFieldName(getCustomDataFieldName())
                .getPermissionStrings();
    }

    /**
     * Returns a set of Shiro {@link Permission} instances stored in the specified {@link CustomData} resource.  This
     * implementation will:
     * <ol>
     * <li>{@link #getPermissionStrings(com.stormpath.sdk.directory.CustomData) Get all permission strings} stored
     * in the CustomData instance</li>
     * <li>Loop over these strings, and for each one, create a {@link Permission} instance using the
     * {@link #getPermissionResolver() permissionResolver} property.</li>
     * <li>Return the total constructed Set of Permission instances to the caller.</li>
     * </ol>
     *
     * @param customData the CustomData instance that may contain permission strings to obtain
     * @return a set of Shiro {@link Permission} instances stored in the specified {@link CustomData} resource.
     */
    protected Set<Permission> getPermissions(CustomData customData) {

        Set<String> permStrings = getPermissionStrings(customData);

        if (CollectionUtils.isEmpty(permStrings)) {
            return Collections.emptySet();
        }

        PermissionResolver permissionResolver = getPermissionResolver();

        Set<Permission> perms = new HashSet<Permission>(permStrings.size());

        for (String s : permStrings) {
            Permission perm = permissionResolver.resolvePermission(s);
            perms.add(perm);
        }

        return perms;
    }
}
