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


import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.application.ApplicationList;
import com.stormpath.sdk.client.Client;
import com.stormpath.sdk.lang.Assert;

import static com.stormpath.shiro.config.DefaultClientFactory.STORMPATH_APPLICATION_HREF;

public class DefaultApplicationResolver implements ApplicationResolver {

    private static final String APP_HREF_ERROR =
            "The application's stormpath.properties configuration does not have a " + STORMPATH_APPLICATION_HREF +
                    " property defined.  This property is required when looking up an application by ServletContext and " +
                    "you have more than one application registered in Stormpath.  For example:\n\n" +
                    " # in stormpath.properties:\n" +
                    " " + STORMPATH_APPLICATION_HREF + " = YOUR_STORMPATH_APPLICATION_HREF_HERE\n";

    @Override
    public Application getApplication(Client client, String href) {
        Application application;

        if (href == null) {

            //no stormpath.application.href property was configured.  Let's try to find their application:

            ApplicationList apps = client.getApplications();
            Application single = null;

            for (Application app : apps) {
                if (app.getName().equalsIgnoreCase("Stormpath")) { //ignore the admin app
                    continue;
                }
                if (single != null) {
                    //there is more than one application in the tenant, and we can't infer which one should be used
                    //for this particular application.  Let them know:
                    throw new IllegalStateException(APP_HREF_ERROR);
                }
                single = app;
            }

            application = single;

        } else {
            Assert.hasText(href, "The specified " + STORMPATH_APPLICATION_HREF + " property value cannot be empty.");

            //now lookup the app (will be cached when caching is turned on in the SDK):
            try {
                application = client.getResource(href, Application.class);
            } catch (Exception e) {
                String msg = "Unable to lookup Stormpath application reference by " + STORMPATH_APPLICATION_HREF +
                        " [" + href + "].  Please ensure this href is accurate and reflects an application " +
                        "registered in Stormpath.";
                throw new IllegalArgumentException(msg, e);
            }
        }

        return application;
    }
}
