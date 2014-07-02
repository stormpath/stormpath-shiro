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
package com.stormpath.shiro.web.model

import org.junit.Test
import static org.junit.Assert.*

/**
 * @since 0.7.0
 */
class RegisterBeanTest {

    @Test
    public void test() {
        def bean = new RegisterBean()
        def email = "someEmail"
        def firstName = "firstName"
        def lastName = "lastName"
        def middleName = "middleName"
        def password = "myPassword"
        def status = "enabled"
        def username = "username"
        bean.setEmail(email)
        bean.setFirstName(firstName)
        bean.setLastName(lastName)
        bean.setMiddleName(middleName)
        bean.setPassword(password)
        bean.setUsername(username)

        assertSame(bean.getEmail(), email)
        assertSame(bean.getFirstName(), firstName)
        assertSame(bean.getLastName(), lastName)
        assertSame(bean.getMiddleName(), middleName)
        assertSame(bean.getPassword(), password)
        assertSame(bean.getUsername(), username)

    }

}
