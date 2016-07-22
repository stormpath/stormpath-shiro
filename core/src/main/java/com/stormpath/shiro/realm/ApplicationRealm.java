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
package com.stormpath.shiro.realm;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.authc.AuthenticationRequest;
import com.stormpath.sdk.client.Client;
import com.stormpath.sdk.group.Group;
import com.stormpath.sdk.group.GroupList;
import com.stormpath.sdk.impl.authc.DefaultUsernamePasswordRequest;
import com.stormpath.sdk.resource.ResourceException;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.AllowAllCredentialsMatcher;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.Permission;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.apache.shiro.util.CollectionUtils;
import org.apache.shiro.util.StringUtils;

import java.util.*;

/**
 * A {@code Realm} implementation that uses the <a href="http://www.stormpath.com">Stormpath</a> Cloud Identity
 * Management service for authentication and authorization operations for a single Application.
 * <p/>
 * Your
 * <a href="http://docs.stormpath.com/rest/product-guide/#application-url">application's Stormpath REST URL</a>
 * must be configured as the {@code applicationRestUrl} property.
 * <h3>Authentication</h3>
 * Once your application's REST URL is configured, this realm implementation automatically executes authentication
 * attempts without any need of further configuration by interacting with the Application's
 * <a href="http://docs.stormpath.com/rest/product-guide/#application-account-authc">loginAttempts endpoint</a>.
 * <h3>Authorization</h3>
 * Stormpath Accounts and Groups can be translated to Shiro roles and permissions via the following components:
 * <ul>
 * <li>{@link AccountPermissionResolver AccountPermissionResolver}</li>
 * <li>{@link GroupPermissionResolver GroupPermissionResolver}</li>
 * <li>{@link GroupRoleResolver GroupRoleResolver}</li>
 * <li>{@link AccountRoleResolver AccountRoleResolver}</li>
 * </ul>
 * <p/>
 * This realm implementation comes pre-configured with the following default implementations, which should suit most
 * Shiro+Stormpath use cases:
 *
 * <table>
 *     <tr>
 *         <th>Property</th>
 *         <th>Pre-configured Implementation</th>
 *         <th>Notes</th>
 *     </tr>
 *     <tr>
 *         <td>{@link #getGroupRoleResolver() groupRoleResolver}</td>
 *         <td>{@link DefaultGroupRoleResolver}</td>
 *         <td>Each Stormpath Group can be represented as up to three possible Shiro roles (with 1-to-1 being the
 *         default).  See the {@link DefaultGroupRoleResolver} JavaDoc for more info.</td>
 *     </tr>
 *     <tr>
 *         <td>{@link #getAccountRoleResolver() accountRoleResolver}</td>
 *         <td>None</td>
 *         <td>Most Shiro+Stormpath applications should only need the above {@code DefaultGroupRoleResolver} when using
 *             Stormpath Groups as Shiro roles.  This realm implementation already acquires the
 *             {@link com.stormpath.sdk.account.Account#getGroups() account's assigned groups} and resolves the group
 *             roles via the above {@code groupRoleResolver}.  <b>You only need to configure this property
 *             if you need an additional way to represent an account's assigned roles that cannot already be
 *             represented via Stormpath account &lt;--&gt; group associations.</td>
 *     </tr>
 *     <tr>
 *         <td>{@link #getGroupPermissionResolver() groupPermissionResolver}</td>
 *         <td>{@link GroupCustomDataPermissionResolver}</td>
 *         <td>The {@code GroupCustomDataPermissionResolver} assumes the convention that a Group's assigned permissions
 *         are stored as a nested {@code Set&lt;String&gt;} field in the
 *         {@link com.stormpath.sdk.group.Group#getCustomData() group's CustomData resource}.  See the
 *         {@link GroupCustomDataPermissionResolver} JavaDoc for more information.</td>
 *     </tr>
 *     <tr>
 *         <td>{@link #getAccountPermissionResolver() accountPermissionResolver}</td>
 *         <td>{@link AccountCustomDataPermissionResolver}</td>
 *         <td>The {@code AccountCustomDataPermissionResolver} assumes the convention that an Account's directly
 *         assigned permissions are stored as a nested {@code Set&lt;String&gt;} field in the
 *         {@link com.stormpath.sdk.account.Account#getCustomData() account's CustomData resource}.  See the
 *         {@link AccountCustomDataPermissionResolver} JavaDoc for more information.</td>
 *     </tr>
 * </table>
 * <h4>Transitive Permissions</h4>
 * This implementation represents an Account's granted permissions as all permissions that:
 * <ol>
 *     <li>Are assigned directly to the Account itself</li>
 *     <li>Are assigned to any of the Account's assigned Groups</li>
 * </ol>
 * <h4>Assigning Permissions</h4>
 * A Shiro Realm is a read-only component - it typically does not support account/group/permission updates directly.
 * Therefore, you make modifications to these components by interacting with the data store (e.g. Stormpath) directly.
 * <p/>
 * The {@link com.stormpath.shiro.authz.CustomDataPermissionsEditor CustomDataPermissionsEditor} has been provided for
 * this purpose.  For example, assuming the convention of storing permissions in an account or group's CustomData
 * resource:
 * <pre>
 * Account account = getAccount();
 * new CustomDataPermissionsEditor(account.getCustomData())
 *     .append("someResourceType:anIdentifier:anAction")
 *     .append("anotherResourceType:anIdentifier:*")
 *     .remove("oldPermission");
 * account.save();
 * </pre>
 * Again, the default {@link #getGroupPermissionResolver() groupPermissionResolver} and
 * {@link #getAccountPermissionResolver() accountPermissionResolver} instances assume this CustomData storage strategy,
 * so if you use them, the above {@code CustomDataPermissionsEditor} will work easily.
 *
 * @see AccountPermissionResolver
 * @see GroupPermissionResolver
 * @see GroupRoleResolver
 * @see AccountRoleResolver
 * @since 0.1
 */
public class ApplicationRealm extends AuthorizingRealm {

    private Client client;
    private String applicationRestUrl;
    private GroupRoleResolver groupRoleResolver;
    private GroupPermissionResolver groupPermissionResolver;
    private AccountPermissionResolver accountPermissionResolver;
    private AccountRoleResolver accountRoleResolver;

    private Application application; //acquired via the client at runtime, not configurable by the Realm user

    public ApplicationRealm() {
        //Stormpath authenticates user accounts directly, no need to perform that here in Shiro:
        setCredentialsMatcher(new AllowAllCredentialsMatcher());
        setGroupRoleResolver(new DefaultGroupRoleResolver());
        setGroupPermissionResolver(new GroupCustomDataPermissionResolver());
        setAccountPermissionResolver(new AccountCustomDataPermissionResolver());
    }

    /**
     * Returns the {@code Client} instance used to communicate with Stormpath's REST API.
     *
     * @return the {@code Client} instance used to communicate with Stormpath's REST API.
     */
    public Client getClient() {
        return client;
    }

    /**
     * Sets the {@code Client} instance used to communicate with Stormpath's REST API.
     *
     * @param client the {@code Client} instance used to communicate with Stormpath's REST API.
     */
    public void setClient(Client client) {
        this.client = client;
    }

    /**
     * Returns the Stormpath REST URL of the specific application communicating with Stormpath.
     * <p/>
     * Any application supported by Stormpath will have a
     * <a href="http://www.stormpath.com/docs/quickstart/authenticate-account">dedicated unique REST URL</a>.  The
     * Stormpath REST URL of the Shiro-enabled application communicating with Stormpath via this Realm must be
     * configured by this property.
     *
     * @return the Stormpath REST URL of the specific application communicating with Stormpath.
     */
    public String getApplicationRestUrl() {
        return applicationRestUrl;
    }

    /**
     * Sets the Stormpath REST URL of the specific application communicating with Stormpath.
     * <p/>
     * Any application supported by Stormpath will have a
     * <a href="http://www.stormpath.com/docs/quickstart/authenticate-account">dedicated unique REST URL</a>.  The
     * Stormpath REST URL of the Shiro-enabled application communicating with Stormpath via this Realm must be
     * configured by this property.
     *
     * @param applicationRestUrl the Stormpath REST URL of the specific application communicating with Stormpath.
     */
    public void setApplicationRestUrl(String applicationRestUrl) {
        this.applicationRestUrl = applicationRestUrl;
    }

    /**
     * Returns the {@link GroupRoleResolver} used to translate Stormpath Groups into Shiro role names.
     * Unless overridden via {@link #setGroupRoleResolver(GroupRoleResolver) setGroupRoleResolver},
     * the default instance is a {@link DefaultGroupRoleResolver}.
     *
     * @return the {@link GroupRoleResolver} used to translate Stormpath Groups into Shiro role names.
     * @since 0.2
     */
    public GroupRoleResolver getGroupRoleResolver() {
        return groupRoleResolver;
    }

    /**
     * Sets the {@link GroupRoleResolver} used to translate Stormpath Groups into Shiro role names.
     * Unless overridden, the default instance is a {@link DefaultGroupRoleResolver}.
     *
     * @param groupRoleResolver the {@link GroupRoleResolver} used to translate Stormpath Groups into Shiro role names.
     * @since 0.2
     */
    public void setGroupRoleResolver(GroupRoleResolver groupRoleResolver) {
        this.groupRoleResolver = groupRoleResolver;
    }

    /**
     * Returns the {@link GroupPermissionResolver} used to discover a Stormpath Groups' assigned permissions.  Unless
     * overridden via {@link #setGroupPermissionResolver(GroupPermissionResolver) setGroupPermissionResolver}, the
     * default instance is a {@link GroupCustomDataPermissionResolver}.
     *
     * @return the {@link GroupPermissionResolver} used to discover a Stormpath Groups' assigned permissions
     * @since 0.2
     */
    public GroupPermissionResolver getGroupPermissionResolver() {
        return groupPermissionResolver;
    }

    /**
     * Sets the {@link GroupPermissionResolver} used to discover a Stormpath Groups' assigned permissions.  Unless
     * overridden, the default instance is a {@link GroupCustomDataPermissionResolver}.
     *
     * @param groupPermissionResolver the {@link GroupPermissionResolver} used to discover a Stormpath Groups' assigned
     *                                permissions
     * @since 0.2
     */
    public void setGroupPermissionResolver(GroupPermissionResolver groupPermissionResolver) {
        this.groupPermissionResolver = groupPermissionResolver;
    }

    /**
     * Returns the {@link AccountPermissionResolver} used to discover a Stormpath Account's directly-assigned
     * permissions.  Unless overridden via
     * {@link #setAccountPermissionResolver(AccountPermissionResolver) setAccountPermissionResolver}, the default
     * instance is a {@link AccountCustomDataPermissionResolver}.
     *
     * @return the {@link AccountPermissionResolver} used to discover a Stormpath Account's assigned permissions.
     * @since 0.3
     */
    public AccountPermissionResolver getAccountPermissionResolver() {
        return accountPermissionResolver;
    }

    /**
     * Sets the {@link AccountPermissionResolver} used to discover a Stormpath Account's assigned permissions.  Unless
     * overridden, the default instance is a {@link AccountCustomDataPermissionResolver}.
     *
     * @param accountPermissionResolver the {@link AccountPermissionResolver} used to discover a Stormpath Account's
     *                                  assigned permissions
     * @since 0.3
     */
    public void setAccountPermissionResolver(AccountPermissionResolver accountPermissionResolver) {
        this.accountPermissionResolver = accountPermissionResolver;
    }

    /**
     * Returns the {@link AccountRoleResolver} used to resolve a Stormpath Account into Shiro role names.  This is
     * {@code null} by default.
     * <p/>
     * <b>You only need to configure this property if you are <em>not</em> using Stormpath Groups as Shiro Roles.</b><br/>
     * Stormpath Account resources are usually associated with one or more Stormpath Groups and those Groups can be
     * represented as Shiro roles by using the {@link #getGroupRoleResolver() groupRoleResolver}.  You only need this
     * component to represent Account Roles that are not already represented as Stormpath Groups via the
     * {@code groupRoleResolver}.
     *
     * @return the {@link AccountRoleResolver} used to resolve a Stormpath Account into Shiro role names.
     * @since 0.2
     */
    public AccountRoleResolver getAccountRoleResolver() {
        return accountRoleResolver;
    }

    /**
     * Sets the {@link AccountRoleResolver} used to resolve a Stormpath Account into Shiro role names.  This is
     * {@code null} by default.
     * <p/>
     * <b>You only need to configure this property if you are <em>not</em> using Stormpath Groups as Shiro Roles.</b><br/>
     * Stormpath Account resources are usually associated with one or more Stormpath Groups and those Groups can be
     * represented as Shiro roles by using the {@link #getGroupRoleResolver() groupRoleResolver}.  You only need this
     * component to represent Account Roles that are not already represented as Stormpath Groups via the
     * {@code groupRoleResolver}.
     *
     * @param accountRoleResolver the {@link AccountRoleResolver} used to resolve a Stormpath Account into Shiro
     *                            role names.
     * @since 0.2
     */
    public void setAccountRoleResolver(AccountRoleResolver accountRoleResolver) {
        this.accountRoleResolver = accountRoleResolver;
    }

    @Override
    protected void onInit() {
        super.onInit();
        assertState();
    }

    private void assertState() {
        if (this.client == null) {
            throw new IllegalStateException("Stormpath SDK Client instance must be configured.");
        }
        if (this.applicationRestUrl == null) {
            throw new IllegalStateException("\n\nThis application's Stormpath REST URL must be configured.\n\n  " +
                    "You may get your application's Stormpath REST URL as shown here:\n\n " +
                    "http://www.stormpath.com/docs/application-rest-url\n\n" +
                    "Copy and paste the 'REST URL' value as the 'applicationRestUrl' property of this class.");
        }
    }

    //this is not thread safe, but the Client is, and this is only executed during initial Application
    //acquisition, so it is negligible if this executes a few times instead of just once.
    protected final Application ensureApplicationReference() {
        if (this.application == null) {
            assertState();
            String href = getApplicationRestUrl();
            this.application = client.getDataStore().getResource(href, Application.class);
        }
        return this.application;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authcToken) throws AuthenticationException {

        assertState();

        UsernamePasswordToken token = (UsernamePasswordToken) authcToken;

        AuthenticationRequest request = createAuthenticationRequest(token);

        Application application = ensureApplicationReference();

        Account account;

        try {
            account = application.authenticateAccount(request).getAccount();
        } catch (ResourceException e) {
            //todo error code translation to throw more detailed exceptions
            String msg = StringUtils.clean(e.getMessage());
            if (msg == null) {
                msg = StringUtils.clean(e.getDeveloperMessage());
            }
            if (msg == null) {
                msg = "Invalid login or password.";
            }
            throw new AuthenticationException(msg, e);
        }

        PrincipalCollection principals;

        try {
            principals = createPrincipals(account);
        } catch (Exception e) {
            throw new AuthenticationException("Unable to obtain authenticated account properties.", e);
        }

        return new SimpleAuthenticationInfo(principals, null);
    }

    protected AuthenticationRequest createAuthenticationRequest(UsernamePasswordToken token) {
        String username = token.getUsername();
        char[] password = token.getPassword();
        String host = token.getHost();

        DefaultUsernamePasswordRequest usernamePasswordRequest = new DefaultUsernamePasswordRequest(username, password);

        if (host != null) {
            usernamePasswordRequest.setHost(host);
        }

        return usernamePasswordRequest;
    }

    protected PrincipalCollection createPrincipals(Account account) {

        LinkedHashMap<String, String> props = new LinkedHashMap<String, String>();

        props.put("href", account.getHref());
        nullSafePut(props, "username", account.getUsername());
        nullSafePut(props, "email", account.getEmail());
        nullSafePut(props, "givenName", account.getGivenName());
        nullSafePut(props, "middleName", account.getMiddleName());
        nullSafePut(props, "surname", account.getSurname());

        Collection<Object> principals = new ArrayList<Object>(2);
        principals.add(account.getHref());
        principals.add(props);

        return new SimplePrincipalCollection(principals, getName());
    }

    private void nullSafePut(Map<String, String> props, String propName, String value) {
        value = StringUtils.clean(value);
        if (value != null) {
            props.put(propName, value);
        }
    }

    protected String getAccountHref(PrincipalCollection principals) {
        Collection c = principals.fromRealm(getName());
        //Based on the createPrincipals implementation above, the first one is the Account href:
        return (String) c.iterator().next();
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {

        assertState();

        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();

        String href = getAccountHref(principals);

        //TODO resource expansion (account + groups in one request instead of two):
        Account account = getClient().getDataStore().getResource(href, Account.class);

        GroupList groups = account.getGroups();

        for (Group group : groups) {
            Set<String> groupRoles = resolveRoles(group);
            for (String roleName : groupRoles) {
                info.addRole(roleName);
            }

            Set<Permission> permissions = resolvePermissions(group);
            for (Permission permission : permissions) {
                info.addObjectPermission(permission);
            }
        }

        //since 0.3:
        Set<String> accountRoles = resolveRoles(account);
        for (String roleName : accountRoles) {
            info.addRole(roleName);
        }

        //since 0.3:
        Set<Permission> accountPermissions = resolvePermissions(account);
        for (Permission permission : accountPermissions) {
            info.addObjectPermission(permission);
        }

        if (CollectionUtils.isEmpty(info.getRoles()) &&
                CollectionUtils.isEmpty(info.getObjectPermissions()) &&
                CollectionUtils.isEmpty(info.getStringPermissions())) {
            //no authorization data associated with the Account
            return null;
        }

        return info;
    }

    //since 0.3
    private Set<Permission> resolvePermissions(Account account) {
        if (accountPermissionResolver != null) {
            return accountPermissionResolver.resolvePermissions(account);
        }
        return Collections.emptySet();
    }

    private Set<Permission> resolvePermissions(Group group) {
        if (groupPermissionResolver != null) {
            return groupPermissionResolver.resolvePermissions(group);
        }
        return Collections.emptySet();
    }

    private Set<String> resolveRoles(Group group) {
        if (groupRoleResolver != null) {
            return groupRoleResolver.resolveRoles(group);
        }
        return Collections.emptySet();
    }

    //since 0.3
    private Set<String> resolveRoles(Account account) {
        if (accountRoleResolver != null) {
            return accountRoleResolver.resolveRoles(account);
        }
        return Collections.emptySet();
    }

    /**
     * If authentication caching is enabled, authentication data for an account must be evicted (removed) from the cached during logout.
     * <p/>
     * Since the user submitted username is different to the the primary account identifier (i.e. username for the former, account href for the latter),
     * we need to overwrite the {@link org.apache.shiro.realm.AuthenticatingRealm#getAuthenticationCacheKey(org.apache.shiro.subject.PrincipalCollection)
     * AuthenticatingRealm#getAuthenticationCacheKey(PrincipalCollection)}.
     * <p/>
     * This guarantees that the same cache key used to cache the data during authentication (derived from the AuthenticationToken)
     * will be used to remove the cached data during logout (derived from the PrincipalCollection).
     * <p/>
     * This is a fix for <a href="https://github.com/stormpath/stormpath-shiro/issues/6">Issue #6</a>.
     *
     * @param principals the collection of all principals associated with the current subject.
     * @return the key used to store the authentication information in the cache (i.e. Stormpath's {@link Account} email)
     * @since 0.6.0
     */
    protected Object getAuthenticationCacheKey(PrincipalCollection principals) {
        if (!CollectionUtils.isEmpty(principals)) {
            Collection thisPrincipals = principals.fromRealm(getName());
            if (!CollectionUtils.isEmpty(thisPrincipals)) {
                Iterator iterator = thisPrincipals.iterator();
                iterator.next(); //First item is the Stormpath' account href
                //Second item is Stormpath' account map
                Map<String, Object> accountInfo = (Map<String, Object>) iterator.next();
                //Users can indistinctively login using their emails or usernames. Therefore, we need to try which is
                //the key used in each case
                String email = (String) accountInfo.get("email");
                if (getAuthenticationCache().get(email) != null) {
                    return email;
                }
                return accountInfo.get("username");
            } else {
                //no principals attributed to this particular realm.  Fall back to the 'master' primary:
                return principals.getPrimaryPrincipal();
            }
        }
        return null;
    }
}
