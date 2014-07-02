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
package com.stormpath.shiro.web.conf;

import com.stormpath.shiro.web.utils.Constants;
import org.apache.shiro.util.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Properties;

/**
 * @since 0.6.0
 */
public class UrlFor {

    private static final Logger logger = LoggerFactory.getLogger(UrlFor.class);

    private static final Properties properties;

    static{
        Properties defaultProperties = new Properties();
        readData(defaultProperties, UrlFor.class.getClassLoader().getResourceAsStream(Constants.DEFAULT_ENDPOINTS_FILE));
        properties = new Properties();
        properties.putAll(defaultProperties);
        InputStream customPropertiesIS = UrlFor.class.getClassLoader().getResourceAsStream(Constants.CUSTOM_ENDPOINTS_FILE);
        if(customPropertiesIS != null) {
            Properties customProperties = new Properties();
            readData(customProperties, customPropertiesIS);
            properties.putAll(customProperties);
        }
    }

    public static String get(String key) {
          return (String) properties.get(key);
    }

    private static void readData(Properties properties, InputStream inputStream) {
        try {
            properties.load(inputStream);
            if (CollectionUtils.isEmpty(properties)) {
                logger.warn("UrlFor resource exists, but it does not contain any data.");
            }
        } catch (IOException io) {
            logger.error(io.getMessage());
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
