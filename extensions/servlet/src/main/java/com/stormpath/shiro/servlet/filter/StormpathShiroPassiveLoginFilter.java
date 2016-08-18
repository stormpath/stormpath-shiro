package com.stormpath.shiro.servlet.filter;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.servlet.account.AccountResolver;
import com.stormpath.shiro.realm.StormpathWebRealm.AccountAuthenticationToken;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.servlet.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;

/**
 * If a Stormpath Account is found via the AccountResolver, and the current subject is NOT already logged in,
 * A login request will be made with a {@link AccountAuthenticationToken}.
 */
public class StormpathShiroPassiveLoginFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(ServletRequest request, ServletResponse response, FilterChain chain) throws ServletException, IOException {

        // if we have a subject and an account, then perform the shiro login

        Subject subject = SecurityUtils.getSubject();
        if (!subject.isAuthenticated()) {
            Account account = AccountResolver.INSTANCE.getAccount(request);
            if (account != null) {
                subject.login(new AccountAuthenticationToken(account));
            }
        }

        chain.doFilter(request, response);
    }
}
