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

import com.stormpath.shiro.web.filter.VelocityFilter;
import org.apache.velocity.Template;
import org.apache.velocity.tools.view.VelocityLayoutServlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @version 0.7.0
 */
public class VelocityServlet extends VelocityLayoutServlet {

    @Override
    public Template getTemplate(HttpServletRequest request, HttpServletResponse response) {
        Template template;
        String templateToLoad = (String)request.getAttribute("javax.servlet.forward.servlet_path");
        if(templateToLoad != null) {
            template = super.getTemplate(templateToLoad);
        } else {
            template = super.getTemplate(request, response);
        }
        return template;
    }

}
