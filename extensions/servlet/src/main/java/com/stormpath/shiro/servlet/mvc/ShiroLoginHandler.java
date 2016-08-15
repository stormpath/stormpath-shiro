package com.stormpath.shiro.servlet.mvc;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.servlet.mvc.WebHandler;
import com.stormpath.shiro.realm.StormpathWebRealm;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.config.ConfigurationException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class ShiroLoginHandler implements WebHandler {
    @Override
    public boolean handle(HttpServletRequest request, HttpServletResponse response, Account account) {

        AuthenticationToken token = new StormpathWebRealm.AccountAuthenticationToken(account);

        try {
            SecurityUtils.getSubject().login(token);
        } catch (AuthenticationException e) {
            String msg = "Unable to pass on authentication info.";
            throw new ConfigurationException(msg, e);
        }

        return true;
    }
}
