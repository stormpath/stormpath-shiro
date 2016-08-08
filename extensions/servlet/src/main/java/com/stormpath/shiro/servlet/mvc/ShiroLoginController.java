package com.stormpath.shiro.servlet.mvc;

import com.stormpath.sdk.servlet.form.Form;
import com.stormpath.sdk.servlet.mvc.DefaultViewModel;
import com.stormpath.sdk.servlet.mvc.LoginController;
import com.stormpath.sdk.servlet.mvc.ViewModel;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * A login controller that calls Shiro's Subject.login with the form fields <code>login</code> and <code>password</code>.
 */
public class ShiroLoginController extends LoginController {

    @Override
    protected ViewModel onValidSubmit(HttpServletRequest request, HttpServletResponse response, Form form) throws Exception {
        String usernameOrEmail = form.getFieldValue("login");
        String password = form.getFieldValue("password");

        String host = request.getRemoteHost();

        AuthenticationToken token = new UsernamePasswordToken(usernameOrEmail, password, false, host);

        try {
            Subject subject = SecurityUtils.getSubject();
            subject.login(token);

            return new DefaultViewModel(getNextUri(request)).setRedirect(true);

        } catch (AuthenticationException e) {

            String msg = "Unable to authenticate account for submitted username [" + usernameOrEmail + "].";
            throw new ServletException(msg, e);

        }
    }

}
