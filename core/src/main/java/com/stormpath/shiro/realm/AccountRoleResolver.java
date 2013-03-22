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

import com.stormpath.sdk.account.Account;

import java.util.Set;

/**
 * An {@code AccountRoleResolver} inspects a Stormpath {@link Account} and translates it to one or more Role names
 * that Shiro can use for role-based access control checks.
 * <p/>
 * <b>Note: This component is only necessary if you are <em>not</em> using Stormpath Groups as Shiro Roles.</b><br/>
 * Stormpath Account resources are usually associated with one or more Stormpath Groups and those Groups can be
 * represented as Shiro roles by using the {@link GroupRoleResolver}.  You only need this component to represent
 * Account roles that are not already represented as Stormpath Groups via the {@link GroupRoleResolver}.
 * <p/>
 * Shiro uses the resulting role names (in addition to any resolved Group role names) to support role-based access
 * control via a {@code subject}'s {@link org.apache.shiro.subject.Subject#hasRole(String) hasRole(String roleName)}
 * checks.
 *
 * @see GroupRoleResolver
 * @see DefaultGroupRoleResolver
 * @since 0.3
 */
public interface AccountRoleResolver {

    /**
     * Inspects a Stormpath {@link Account} and resolves it to one or more role names
     * that Shiro can use for role-based access control checks.
     * <p/>
     * <b>This is only necessary if you are <em>not</em> using Stormpath Groups as Shiro Roles.</b><br/>Stormpath
     * Account resources are usually associated with one or more Stormpath Groups and those Groups can be represented as
     * Shiro roles by using the {@link GroupRoleResolver}.  You only need this component to represent Account roles that
     * are not already represented as Stormpath Groups via the {@link GroupRoleResolver}.
     * <p/>
     * Shiro uses the resulting role names (in addition to any resolved Group role names) to support role-based access
     * control via a {@code subject}'s {@link org.apache.shiro.subject.Subject#hasRole(String) hasRole(String roleName)}
     * checks.
     *
     * @param account the {@code Stormpath} account to resolve to Shiro role names.
     * @return a set role names attributed to the account, to be used by Shiro for runtime role checks.
     * @see GroupRoleResolver
     * @see DefaultGroupRoleResolver
     */
    Set<String> resolveRoles(Account account);
}
