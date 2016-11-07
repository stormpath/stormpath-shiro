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
package com.stormpath.shiro.servlet.event

import com.stormpath.shiro.servlet.ShiroTestSupport
import org.apache.shiro.subject.Subject
import org.apache.shiro.util.ThreadContext
import org.testng.annotations.Test

import static org.easymock.EasyMock.*

/**
 * Tests for {@link LogoutEventListener}.
 */
class LogoutEventListenerTest extends ShiroTestSupport {

    @Test
    void testNoSubjectOnLogout() {

        def logoutEventListener = new LogoutEventListener()
        logoutEventListener.onLogout(null) // no subject is bound, this should return without issue.
    }

    @Test
    void testSubjectLogout() {

        def subject = createMock(Subject)
        subject.logout()
        def principal = createMock(Object.class)
        expect(subject.getPrincipal()).andReturn(principal);
        replay subject, principal

        ThreadContext.bind(subject)
        def logoutEventListener = new LogoutEventListener()
        logoutEventListener.onLogout(null)

        verify subject, principal
    }
}
