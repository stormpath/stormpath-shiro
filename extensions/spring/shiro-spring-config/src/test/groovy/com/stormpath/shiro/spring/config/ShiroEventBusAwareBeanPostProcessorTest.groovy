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
package com.stormpath.shiro.spring.config

import org.apache.shiro.event.EventBus
import org.apache.shiro.event.EventBusAware
import org.testng.annotations.Test
import static org.easymock.EasyMock.*
import static org.testng.Assert.*

/**
 * Tests for {@link ShiroEventBusBeanPostProcessor}
 */
class ShiroEventBusAwareBeanPostProcessorTest {

    @Test
    void testPostConstructNonAware() {

        def eventBus = createStrictMock(EventBus)
        def bean = createStrictMock(Object)

        replay eventBus, bean

        def postProcessor = new ShiroEventBusBeanPostProcessor(eventBus);
        def resultAfter = postProcessor.postProcessAfterInitialization(bean, "bean")
        def resultBefore = postProcessor.postProcessBeforeInitialization(bean, "bean")

        verify eventBus, bean
        assertSame resultAfter, bean
        assertSame resultBefore, bean
    }

    @Test
    void testPostConstructWithEventBusAware() {

        def eventBus = createStrictMock(EventBus)
        def bean = createStrictMock(EventBusAware)
        bean.eventBus = eventBus

        replay eventBus, bean

        def postProcessor = new ShiroEventBusBeanPostProcessor(eventBus);
        def resultAfter = postProcessor.postProcessAfterInitialization(bean, "bean")
        def resultBefore = postProcessor.postProcessBeforeInitialization(bean, "bean")

        verify eventBus, bean
        assertSame resultAfter, bean
        assertSame resultBefore, bean
    }

}
