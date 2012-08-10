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

import java.util.Set;

/**
 * A {@code GroupRoleResolver} inspects a Stormpath {@link Group} and translates it to one or more Role names that Shiro
 * can use for role-based access control checks.
 * <p/>
 * Shiro does not distinguish the difference between the concept of a 'group' or 'role' - it just happens to call this
 * concept a 'role', which to Shiro, is a simple String name.
 * <p/>
 * Because Stormpath uses a {@link Group} concept to represent both Groups and Roles, this component allows one to
 * translate Stormpath's Group concept into one or more Shiro role concepts.
 * <p/>
 * Shiro uses the resulting role names to support role-based access control via a
 * {@code subject}'s {@link org.apache.shiro.subject.Subject#hasRole(String) hasRole(String roleName)} checks.
 *
 * @since 0.2
 */
public interface GroupRoleResolver {

    /**
     * Translates a Stormpath Group entity into one or more Shiro role names.  Shiro uses the resulting role names to
     * support role-based access control via a {@code subject}'s
     * {@link org.apache.shiro.subject.Subject#hasRole(String) hasRole(String roleName)} checks.
     *
     * @param group the {@code Stormpath} group to translate into Shiro role names.
     * @return a set role names attributed to the group, to be used by Shiro for runtime role checks.
     */
    Set<String> resolveRoles(Group group);

}
