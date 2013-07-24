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
package com.stormpath.shiro.realm;

import com.stormpath.sdk.account.Account;
import org.apache.shiro.authz.Permission;

import java.util.Set;

/**
 * An {@code AccountPermissionResolver} inspects a Stormpath {@link Account} and returns that {@code Account}'s
 * directly assigned {@link org.apache.shiro.authz.Permission}s.
 * <p/>
 * Note that this interface is for resolving permissions that are directly assigned to an Account.  Permissions
 * that are assigned to an account's groups (and therefore implicitly associated with an Account), would be resolved
 * instead by a {@link GroupPermissionResolver} instance instead.
 * <p/>
 * Shiro checks these permissions (in addition to any assigned groups' permissions) to determine whether or not a
 * {@link org.apache.shiro.subject.Subject Subject} representing the {@code Account}
 * {@link org.apache.shiro.subject.Subject#isPermitted(org.apache.shiro.authz.Permission) isPermitted} to do something.
 *
 * @since 0.3
 * @see GroupPermissionResolver
 */
public interface AccountPermissionResolver {

    /**
     * Returns a set of {@link org.apache.shiro.authz.Permission Permission}s assigned to a particular Stormpath
     * {@link Account}.
     * <p/>
     * Note that method is for resolving permissions that are directly assigned to an Account.  Permissions
     * that are assigned to an account's groups (and therefore implicitly associated with an Account), would be resolved
     * instead by a {@link GroupPermissionResolver} instance instead.
     * <p/>
     * Shiro checks these permissions to determine whether or not a {@link org.apache.shiro.subject.Subject Subject}
     * representing the {@code Account}
     * {@link org.apache.shiro.subject.Subject#isPermitted(org.apache.shiro.authz.Permission) isPermitted} to do
     * something.
     *
     * @param account the Stormpath {@code Account} to inspect to return its directly assigned Shiro Permissions.
     * @return a set of Shiro {@link org.apache.shiro.authz.Permission Permission}s assigned to the account, to be
     *         used by Shiro for runtime permission checks.
     * @see GroupPermissionResolver
     */
    Set<Permission> resolvePermissions(Account account);
}
