/*
 * Copyright 2013 Stormpath, Inc.
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

import com.stormpath.shiro.web.conf.UrlFor;
import com.stormpath.shiro.web.service.CustomDataService;
import org.apache.shiro.util.StringUtils;
import org.apache.shiro.web.util.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * Endpoint responding to custom data requests from the UI.
 * @version 0.7.0
 */
public class CustomDataServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(CustomDataServlet.class);

    private static final String SERVLET_URI = UrlFor.get("customdata.action");

    /**
     * Insertion of custom data fields is handled here relying on the {@link CustomDataService} to actually do it. The
     * updated custom data field value will be returned in the response so the UI can display it.
     *
     * @param request	an {@link javax.servlet.http.HttpServletRequest} object that contains the request the client has made
     *			of the servlet
     *
     * @param response	an {@link javax.servlet.http.HttpServletResponse} object that contains the field that has just been added/updated and
     *              is sent to the client
     *
     * @exception java.io.IOException	if an input or output error is detected when the servlet handles
     *				the POST request
     *
     * @exception javax.servlet.ServletException	if the request for the POST could not be handled
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String uri = request.getRequestURI();
        String[] splittedUri = StringUtils.split(uri, '/');
        if(uri.startsWith(SERVLET_URI) && splittedUri.length == 3) {
            try {
                Map<String, Object> map = new HashMap<String, Object>();
                Enumeration<String> enumeration = request.getParameterNames();
                for(; enumeration.hasMoreElements() ;) {
                    String name = enumeration.nextElement();
                    map.put(name, request.getParameter(name));
                }
                CustomDataService.getInstance().putAll(map);
                WebUtils.issueRedirect(request, response, UrlFor.get("dashboard"));
                return;
            } catch (Exception ex) {
                logger.warn(ex.getMessage());
                throw new ServletException(ex);
            }
        } else {
            throw new ServletException("This servlet cannot handle this POST request: " + uri);
        }
    }

}
