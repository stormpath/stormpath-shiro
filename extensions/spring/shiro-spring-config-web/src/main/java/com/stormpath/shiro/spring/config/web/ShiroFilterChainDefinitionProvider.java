package com.stormpath.shiro.spring.config.web;


import java.util.Map;

public interface ShiroFilterChainDefinitionProvider {

    Map<String, String> getFilterChainDefinition();
}
