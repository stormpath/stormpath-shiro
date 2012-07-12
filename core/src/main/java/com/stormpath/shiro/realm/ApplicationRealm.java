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
import com.stormpath.sdk.authc.UsernamePasswordRequest;
import com.stormpath.sdk.client.Client;
import com.stormpath.sdk.group.Group;
import com.stormpath.sdk.group.GroupList;
import com.stormpath.sdk.resource.ResourceException;
import org.apache.shiro.authc.*;
import org.apache.shiro.authc.credential.AllowAllCredentialsMatcher;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.apache.shiro.util.CollectionUtils;
import org.apache.shiro.util.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A {@code Realm} implementation that uses the <a href="http://www.stormpath.com">Stormpath</a> Cloud Identity
 * Management service for authentication and authorization operations for a single Application.
 * <p/>
 * The Stormpath-registered Application's Stormpath REST URL must be configured as the
 * {@code applicationRestUrl} property.
 *
 * @since 0.1
 */
public class ApplicationRealm extends AuthorizingRealm {

    private String applicationRestUrl;
    private Client client;

    private Application application; //acquired via the client at runtime

    public ApplicationRealm() {
        //Stormpath authenticates user accounts directly, no need to perform that here:
        setCredentialsMatcher(new AllowAllCredentialsMatcher());
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
    private Application ensureApplicationReference() {
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

        String username = token.getUsername();
        char[] password = token.getPassword();
        String host = token.getHost();

        UsernamePasswordRequest request = new UsernamePasswordRequest(username, password, host);

        Application application = ensureApplicationReference();

        Account account;

        try {
            account = application.authenticate(request);
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

        String href = getAccountHref(principals);

        Account account = getClient().getDataStore().getResource(href, Account.class); //TODO resource expansion (account + groups in one request)

        GroupList groups = account.getGroups();

        if (groups == null) {
            return null;
        }

        //translate Stormpath Groups into Shiro Roles:

        //we use a heuristic:
        //For convenience, we allow a 'role' to be any of the following:
        // 1. A Group's fully qualified href
        // 2. A Group's ID (last token in the href)
        // 3. A Group's name (not so safe if the group name changes, HREF/ID will never change)

        //So a Stormpath Group can translate into 3 separate roles.  The developer can choose which he wants to check.

        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();

        for (Group group : groups) {
            String groupHref = group.getHref();

            if (groupHref != null) {
                info.addRole(groupHref);
                String instanceId = getInstanceId(groupHref);
                if (instanceId != null) {
                    info.addRole(instanceId);
                }
            }

            String name = group.getName();
            if (name != null) {
                info.addRole(name);
            }
        }

        if (CollectionUtils.isEmpty(info.getRoles())) {
            //no groups associated with the Account
            return null;
        }

        return info;
    }

    private String getInstanceId(String href) {
        if (href != null) {
            int i = href.lastIndexOf('/');
            if (i >= 0) {
                return href.substring(i);
            }
        }
        return null;
    }
}
