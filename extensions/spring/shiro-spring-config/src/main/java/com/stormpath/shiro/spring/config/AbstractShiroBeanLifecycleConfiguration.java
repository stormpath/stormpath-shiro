package com.stormpath.shiro.spring.config;

import org.apache.shiro.spring.LifecycleBeanPostProcessor;

public class AbstractShiroBeanLifecycleConfiguration {

    public LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
        return new LifecycleBeanPostProcessor();
    }
}
