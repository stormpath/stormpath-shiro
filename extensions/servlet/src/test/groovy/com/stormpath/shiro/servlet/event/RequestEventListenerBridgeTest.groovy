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

import com.stormpath.sdk.servlet.account.event.RegisteredAccountRequestEvent
import com.stormpath.sdk.servlet.account.event.VerifiedAccountRequestEvent
import com.stormpath.sdk.servlet.authc.FailedAuthenticationRequestEvent
import com.stormpath.sdk.servlet.authc.LogoutRequestEvent
import com.stormpath.sdk.servlet.authc.SuccessfulAuthenticationRequestEvent
import org.apache.shiro.event.EventBus
import org.easymock.Capture
import org.testng.Assert
import org.testng.annotations.DataProvider
import org.testng.annotations.Test
import static org.easymock.EasyMock.*

/**
 * Tests for @{link RequestEventListenerBridge}.
 * @since 0.7
 */
class RequestEventListenerBridgeTest {

    @Test
    void testPublishWithNullEventBus() {
        def eventBridge = new RequestEventListenerBridge()
        eventBridge.eventBus = null

        eventBridge.publishEvent(new Object())
        // Pass, as long as we don't hit an NPE
    }

    @Test(dataProvider = "eventClasses")
    void testBasicEventsFired(Class eventClass) {

        def eventUnderTest = createMock(eventClass)

        def eventCapture = new Capture()
        def eventBus = createMock(EventBus)
        eventBus.publish(capture(eventCapture))
        replay eventBus, eventUnderTest

        def eventBridge = new RequestEventListenerBridge()
        eventBridge.eventBus = eventBus

        eventBridge.on(eventUnderTest)

        verify eventBus, eventUnderTest
        Assert.assertSame(eventCapture.getValue(), eventUnderTest)
    }

    @DataProvider(name = "eventClasses")
    public Object[][] listEventClasses() {
        return [
                [SuccessfulAuthenticationRequestEvent],
                [FailedAuthenticationRequestEvent],
                [RegisteredAccountRequestEvent],
                [VerifiedAccountRequestEvent],
                [LogoutRequestEvent]
        ]
    }



}
