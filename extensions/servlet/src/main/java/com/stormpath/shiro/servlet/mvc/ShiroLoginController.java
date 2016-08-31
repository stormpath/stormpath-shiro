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
 *
 * This will be removed before the 0.7.0 release, keeping for now as it was referenced in a support request ticket.
 * @deprecated replaced with {@link com.stormpath.shiro.servlet.filter.StormpathShiroPassiveLoginFilter}.
 */
@Deprecated
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
