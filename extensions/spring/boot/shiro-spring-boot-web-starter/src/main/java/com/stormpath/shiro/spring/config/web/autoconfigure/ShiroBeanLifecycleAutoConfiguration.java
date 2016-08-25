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
package com.stormpath.shiro.spring.config.web.autoconfigure;

import com.stormpath.shiro.spring.config.AbstractShiroBeanLifecycleConfiguration;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @since 0.7.0
 */
@Configuration
@ConditionalOnProperty(name = "shiro.web.enabled", matchIfMissing = true)
public class ShiroBeanLifecycleAutoConfiguration extends AbstractShiroBeanLifecycleConfiguration {

    @Bean
    public LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
        return super.lifecycleBeanPostProcessor();
    }
}
