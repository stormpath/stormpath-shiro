package com.stormpath.shiro.realm;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.oauth.AccessTokenResult;
import org.apache.shiro.authc.*;
import org.apache.shiro.authc.credential.AllowAllCredentialsMatcher;
import org.apache.shiro.subject.PrincipalCollection;

/**
 * Realm implantation that pushes a stormpath-servlet authenticated/authorized Account into a Shiro Subject.
 */
public class StormpathWebRealm extends ApplicationRealm {

    public StormpathWebRealm() {
        super();
        this.setCredentialsMatcher(new AllowAllCredentialsMatcher());
        this.setAuthenticationTokenClass(AccessTokenResultAuthToken.class);
        this.setCachingEnabled(false);
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {

        AccessTokenResultAuthToken accessAuthToken = (AccessTokenResultAuthToken) token;

        PrincipalCollection principals;

        try {
            principals = createPrincipals(accessAuthToken.getAccount());
        } catch (Exception e) {
            throw new AuthenticationException("Unable to obtain authenticated account properties.", e);
        }

        return new SimpleAuthenticationInfo(principals, null);

    }


    public static class AccessTokenResultAuthToken implements AuthenticationToken {

        final private Account account;


        public AccessTokenResultAuthToken(AccessTokenResult accessTokenResult) {
            this.account = accessTokenResult.getAccount();
        }

        public AccessTokenResultAuthToken(Account account) {
            this.account = account;
        }

        public Account getAccount() {
            return account;
        }

        @Override
        public Object getPrincipal() {
            return getAccount();
        }

        @Override
        public Object getCredentials() {
            return null;
        }
    }
}
