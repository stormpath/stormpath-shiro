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
package com.stormpath.shiro.servlet.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @since 0.7.0
 */
public class Constants {
    private static final Logger logger = LoggerFactory.getLogger(Constants.class);

    public static final String STORMPATH_BASE_URL = "https://api.stormpath.com/v1/";

    public static final String DEFAULT_CONFIG_INI_FILE = "defaultConfig.ini";
    public static final String CUSTOM_CONFIG_INI_FILE = "config.ini";

    public static final String DEFAULT_ENDPOINTS_FILE = "defaultEndpoints.properties";
    public static final String CUSTOM_ENDPOINTS_FILE = "endpoints.properties";
}
