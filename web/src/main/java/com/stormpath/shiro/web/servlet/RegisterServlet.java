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
package com.stormpath.shiro.web.servlet;

import com.stormpath.sdk.resource.ResourceException;
import com.stormpath.shiro.web.conf.UrlFor;
import com.stormpath.shiro.web.model.RegisterBean;
import com.stormpath.shiro.web.service.RegisterService;
import com.stormpath.shiro.web.utils.Constants;
import org.apache.shiro.web.util.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Endpoint responding to Registration requests from the UI. The account-creation process will be handed over to the
 * {@link com.stormpath.shiro.web.service.RegisterService}.
 *
 * @since 0.7.0
 */
public class RegisterServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(RegisterServlet.class);

    // This servlet will only handle requests through this URI.
    private static final String REGISTRATION_URI = UrlFor.get("register.action");

    /**
     * Registration requests are handled here relying on the {@link com.stormpath.shiro.web.service.RegisterService} to actually execute it.
     *
     * @param req	an {@link javax.servlet.http.HttpServletRequest} object that contains the request the client has made of the servlet.
     * @param resp	an {@link javax.servlet.http.HttpServletResponse} object that contains the response the servlet sends to the client.
     * @exception java.io.IOException	if an input or output error is detected when the servlet handles the POST request
     * @exception javax.servlet.ServletException	if the request for the POST could not be handled
     */
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String uri = req.getRequestURI();
        if(uri.startsWith(REGISTRATION_URI)) {
            try {
                //Let's clear the registration error flag before proceeding...
                getServletContext().removeAttribute(Constants.REGISTRATION_ERROR_FLAG);
                RegisterBean registerBean = new RegisterBean();
                registerBean.setEmail(req.getParameter("email"));
                registerBean.setUsername(req.getParameter("username"));
                registerBean.setPassword(req.getParameter("password"));
                registerBean.setFirstName(req.getParameter("firstName"));
                registerBean.setMiddleName(req.getParameter("middleName"));
                registerBean.setLastName(req.getParameter("lastName"));
                logger.debug("Registering user: " + registerBean.getFirstName() + " " + registerBean.getLastName() + ", with email: " + registerBean.getEmail());
                RegisterService.getInstance().createAccount(registerBean);
                WebUtils.issueRedirect(req, resp, UrlFor.get("dashboard"));
            } catch (ResourceException e) {
                //If an error is found, it is stored so it can be displayed to the user in the UI.
                getServletContext().setAttribute(Constants.REGISTRATION_ERROR_FLAG, e.getDeveloperMessage());
                WebUtils.issueRedirect(req, resp, UrlFor.get("register"));
            }
        } else {
            throw new ServletException("This servlet cannot handle this POST request: " + uri);
        }
    }

}
