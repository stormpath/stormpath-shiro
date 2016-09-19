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

import com.stormpath.sdk.group.Group;
import org.apache.shiro.authz.Permission;

import java.util.Set;

/**
 * A {@code GroupPermissionResolver} inspects a Stormpath {@link Group} and returns that {@code Group}'s assigned
 * {@link Permission}s.
 * <p/>
 * Shiro checks these permissions to determine whether or not a {@link org.apache.shiro.subject.Subject Subject}
 * associated with the {@code Group}
 * {@link org.apache.shiro.subject.Subject#isPermitted(Permission) isPermitted} to do something.
 *
 * @see AccountPermissionResolver
 * @since 0.2
 */
public interface GroupPermissionResolver {

    /**
     * Returns a set of {@link Permission Permission}s assigned to a particular Stormpath {@link Group}.
     * <p/>
     * Shiro checks these permissions to determine whether or not a {@link org.apache.shiro.subject.Subject Subject}
     * associated with the {@code Group} {@link org.apache.shiro.subject.Subject#isPermitted(Permission) isPermitted}
     * to do something.
     *
     * @param group the Stormpath {@code Group} to inspect to return its assigned Shiro Permissions.
     * @return a set of Shiro {@link Permission Permission}s assigned to the group, to be used by Shiro for runtime
     *         permission checks.
     * @see AccountPermissionResolver
     */
    Set<Permission> resolvePermissions(Group group);
}
