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
import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.authc.AuthenticationRequest;
import com.stormpath.sdk.authc.UsernamePasswordRequest;
import com.stormpath.sdk.client.Client;
import com.stormpath.sdk.group.Group;
import com.stormpath.sdk.group.GroupList;
import com.stormpath.sdk.resource.ResourceException;
import org.apache.shiro.authc.*;
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
 * The Stormpath-registered
 * <a href="https://www.stormpath.com/docs/libraries/application-rest-url">Application's Stormpath REST URL</a>
 * must be configured as the {@code applicationRestUrl} property.
 *
 * @since 0.1
 */
public class ApplicationRealm extends AuthorizingRealm {

    private Client client;
    private String applicationRestUrl;
    private GroupRoleResolver groupRoleResolver;
    private GroupPermissionResolver groupPermissionResolver;

    private Application application; //acquired via the client at runtime, not configurable by the Realm user

    public ApplicationRealm() {
        //Stormpath authenticates user accounts directly, no need to perform that here in Shiro:
        setCredentialsMatcher(new AllowAllCredentialsMatcher());
        setGroupRoleResolver(new DefaultGroupRoleResolver());
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
     * Returns the {@link GroupPermissionResolver} used to discover a Stormpath Groups' assigned permissions.  This
     * is {@code null} by default and must be configured based on your application's needs.
     *
     * @return the {@link GroupPermissionResolver} used to discover a Stormpath Groups' assigned permissions
     * @since 0.2
     */
    public GroupPermissionResolver getGroupPermissionResolver() {
        return groupPermissionResolver;
    }

    /**
     * Sets the {@link GroupPermissionResolver} used to discover a Stormpath Groups' assigned permissions.  This
     * is {@code null} by default and must be configured based on your application's needs.
     *
     * @param groupPermissionResolver the {@link GroupPermissionResolver} used to discover a Stormpath Groups' assigned
     *                                permissions
     * @since 0.2
     */
    public void setGroupPermissionResolver(GroupPermissionResolver groupPermissionResolver) {
        this.groupPermissionResolver = groupPermissionResolver;
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
                    "You may get your application's Stormpath REST URL by logging in to the Stormpath web console " +
                    "and viewing the application's details as shown in step 1.b. here:\n\n " +
                    "http://www.stormpath.com/docs/quickstart/authenticate-account\n\n" +
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
        return new UsernamePasswordRequest(username, password, host);
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

        String href = getAccountHref(principals);

        //TODO resource expansion (account + groups in one request instead of two):
        Account account = getClient().getDataStore().getResource(href, Account.class);

        GroupList groups = account.getGroups();

        if (groups == null) {
            return null;
        }

        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();

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

        if (CollectionUtils.isEmpty(info.getRoles()) &&
                CollectionUtils.isEmpty(info.getObjectPermissions()) &&
                CollectionUtils.isEmpty(info.getStringPermissions())) {
            //no authorization data associated with the Account
            return null;
        }

        return info;
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
}
