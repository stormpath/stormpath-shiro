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
package com.stormpath.shiro

import com.stormpath.sdk.api.ApiKeys
import com.stormpath.sdk.cache.Caches
import com.stormpath.sdk.client.Client
import com.stormpath.sdk.client.Clients
import com.stormpath.sdk.resource.Deletable
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.testng.annotations.AfterTest
import org.testng.annotations.BeforeTest

abstract class ClientIT {

    private static final Logger log = LoggerFactory.getLogger(ClientIT)

    static String apiKeyFileLocation = System.getProperty('user.home') + "/.stormpath/apiKey.properties"
    static Client client

    List<Deletable> resourcesToDelete;

    @BeforeTest
    static void setupClient() {
        client = buildClient();
    }

    @BeforeTest
    public void setUp() {
        resourcesToDelete = []
    }

    @AfterTest
    public void tearDown() {
        def reversed = resourcesToDelete.reverse() //delete in opposite order (cleaner - children deleted before parents)

        for (def r : reversed) {
            try {
                r.delete()
            } catch (Throwable t) {
                log.error('Unable to delete resource ' + r, t)
            }
        }
    }

    protected void deleteOnTeardown(Deletable d) {
        this.resourcesToDelete.add(d)
    }

    //NOTE ABOUT THE STORMPATH_API_KEY_ID and STORMPATH_API_KEY_SECRET env vars below:
    // you can either set them in the OS, or, if you're on the Stormpath dev team, set it in IntelliJ's defaults:
    // Run/Debug Configurations -> Edit Configurations -> Defaults -> TestNG (add the two env vars).  All new tests
    // created in IntelliJ after that point will pick up these vars.
    static Client buildClient(boolean enableCaching=true) {

        def builder = Clients.builder()

        //see if the api key file exists first - if so, use it:
        def file = new File(apiKeyFileLocation)
        if (file.exists() && file.isFile() && file.canRead()) {
            builder.setApiKey(ApiKeys.builder().setFileLocation(apiKeyFileLocation).build())
        } else {
            //no file - check env vars.  This is mostly just so we can pick up encrypted env vars when running on Travis CI:
            String apiKeyId = System.getenv('STORMPATH_API_KEY_ID')
            String apiKeySecret = System.getenv('STORMPATH_API_KEY_SECRET')
            builder.setApiKey(ApiKeys.builder().setId(apiKeyId).setSecret(apiKeySecret).build())
        }

        if (enableCaching) {
            builder.setCacheManager(Caches.newCacheManager().build())
        }

        return builder.build()
    }

    protected static String uniquify(String s) {
        return s + "-" + UUID.randomUUID().toString().replace('-', '');
    }
}
