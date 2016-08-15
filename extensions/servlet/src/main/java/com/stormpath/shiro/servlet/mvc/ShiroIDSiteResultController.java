package com.stormpath.shiro.servlet.mvc;

import com.stormpath.sdk.idsite.AuthenticationResult;
import com.stormpath.sdk.servlet.mvc.IdSiteResultController;
import com.stormpath.sdk.servlet.mvc.ViewModel;
import com.stormpath.shiro.realm.StormpathWebRealm;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.config.ConfigurationException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class ShiroIDSiteResultController extends IdSiteResultController {

    @Override
    protected ViewModel onAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationResult result) {
        ViewModel vm = super.onAuthentication(request, response, result);

        AuthenticationToken token = new StormpathWebRealm.AccountAuthenticationToken(result.getAccount());

        try {
            SecurityUtils.getSubject().login(token);
        } catch (AuthenticationException e) {
            String msg = "Stormpath Shiro realm is not configured correctly, see documentation for: " + StormpathWebRealm.class.getName();
            throw new ConfigurationException(msg, e);
        }

        return vm;
    }
}
