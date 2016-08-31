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
package com.stormpath.shiro.servlet.event;


import com.stormpath.sdk.servlet.authc.LogoutRequestEvent;
import org.apache.shiro.event.Subscribe;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ThreadContext;

/**
 * An event listener that will log out a Shiro {@link Subject} when a to Stormpath
 * {@link LogoutRequestEvent} is published.
 *
 * @since 0.7
 */
public class LogoutEventListener {

    /**
     * Logs out the current Subject (if any) when a {@link LogoutRequestEvent} is published.
     * @param event The logout event.
     */
    @Subscribe
    public void onLogout(LogoutRequestEvent event) {
        Subject subject = ThreadContext.getSubject();
        if (subject != null) {
            subject.logout();
        }
    }
}
