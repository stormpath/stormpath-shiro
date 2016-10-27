package com.stormpath.shiro.samples.dropwizard.bundle.shiro;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Dropwizard Shiro Configuration.
 */
@SuppressWarnings("PMD")
public class ShiroConfiguration {

    @JsonProperty("filterUrlPattern")
    private String filterUrlPattern = "/*";

    public String filterUrlPattern() {
        return filterUrlPattern;
    }

}
