package com.stormpath.shiro.spring.config.web;


import java.util.HashMap;
import java.util.Map;

public class DefaultShiroFilterChainDefinitionProvider implements ShiroFilterChainDefinitionProvider {

    final private Map<String, String> filterChainDefinitionMap = new HashMap<>();

    public void addPathDefinition(String antPath, String definition) {
        filterChainDefinitionMap.put(antPath, definition);
    }

    public void addPathDefinitions(Map<String, String> pathDefinitions) {
        filterChainDefinitionMap.putAll(pathDefinitions);
    }

    @Override
    public Map<String, String> getFilterChainDefinition() {
        return filterChainDefinitionMap;
    }
}
