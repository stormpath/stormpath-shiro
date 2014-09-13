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
package com.stormpath.shiro.servlet.conf;

import com.stormpath.sdk.lang.Assert;
import com.stormpath.shiro.servlet.utils.Constants;
import org.apache.shiro.util.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;

/**
 * Reads the information stored in <code>resources/defaultConfig.ini</code> and then
 * <code>resources/config.ini</code> overriding colliding configuration keys found in
 * <code>defaultEndpoints.properties</code>.
 * <p/>
 * For example, the default configuration expects the application to be running at <code>http://localhost:8080</code> as configured in
 * the <code>baseUrl</code> key. If your application runs in a different URL, you can just create a <code>resources/config.ini</code>
 * file in your application and include this:
 * <pre>
 *  ###### Custom configuration ######
 *  [App]
 *  baseUrl = http://localhost:8080
 * </pre>
 * Then, when the application executes <code>Configuration.getBaseUrl()</code>, it will get the newly configured URL.
 *
 * @since 0.7.0
 */
public class Configuration {
    private static final Logger logger = LoggerFactory.getLogger(Configuration.class);

    private static final OverridableIni ini;

    static{
        OverridableIni defaultsIni = new OverridableIni();
        readIniData(defaultsIni, Constants.DEFAULT_CONFIG_INI_FILE);
        ini = new OverridableIni(defaultsIni);
        readIniData(ini, Constants.CUSTOM_CONFIG_INI_FILE);
        validate();
    }

    //App
    public static String getBaseUrl() {
        return ini.getSection("App").get("baseUrl");
    }

    //ID Site
    public static boolean isIDSiteEnabled() {
        return Boolean.parseBoolean(ini.getSection("ID Site").get("enable"));
    }

    public static String getLoginRedirectUri() {
        return ini.getSection("ID Site").get("loginRedirectUri");
    }

    public static String getLogoutRedirectUri() {
        return ini.getSection("ID Site").get("logoutRedirectUri");
    }


    private static void validate() {
        Assert.hasText(getBaseUrl());
        Assert.notNull(isIDSiteEnabled());
        Assert.hasText(getLoginRedirectUri());
        Assert.hasText(getLogoutRedirectUri());
    }


    private static void readIniData(OverridableIni ini, String inputStreamPath) {
        Assert.hasText(inputStreamPath);
        InputStream inputStream = Configuration.class.getClassLoader().getResourceAsStream(inputStreamPath);

        if (inputStream != null) {
            try {
                ini.load(inputStream);
                if (CollectionUtils.isEmpty(ini)) {
                    logger.warn("Configuration INI resource exists, but it does not contain any data.");
                }
            } catch (Exception e) {
                logger.warn("There was some problem reading configuration file " + inputStreamPath + "." + e.getMessage());
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        logger.error(e.getMessage());
                    }
                }
            }
        }
    }

}
