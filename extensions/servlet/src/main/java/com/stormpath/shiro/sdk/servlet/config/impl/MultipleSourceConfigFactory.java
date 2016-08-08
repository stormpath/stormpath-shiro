/*
 * Copyright 2015 Stormpath, Inc.
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
package com.stormpath.shiro.sdk.servlet.config.impl;

import com.stormpath.sdk.impl.config.*;
import com.stormpath.sdk.impl.io.ClasspathResource;
import com.stormpath.sdk.impl.io.Resource;
import com.stormpath.sdk.impl.io.ResourceFactory;
import com.stormpath.sdk.impl.io.StringResource;
import com.stormpath.sdk.lang.Strings;
import com.stormpath.sdk.servlet.config.Config;
import com.stormpath.sdk.servlet.config.ConfigFactory;
import com.stormpath.sdk.servlet.config.impl.DefaultConfig;
import com.stormpath.sdk.servlet.io.ServletContainerResourceFactory;

import javax.servlet.ServletContext;
import java.util.*;

import static com.stormpath.sdk.servlet.config.impl.DefaultConfigFactory.*;


public class MultipleSourceConfigFactory implements ConfigFactory {

    private static final String REQUIRED_TOKEN = "(required)";

    private static final EnvVarNameConverter envVarNameConverter = new DefaultEnvVarNameConverter();


    private static final String NL                                  = "\n";
    public static final String INTERNAL_TOKEN                       = "internal";
    public static final String DEFAULT_STORMPATH_PROPERTIES_SOURCES =
                    //MUST always be first:
                    ClasspathResource.SCHEME_PREFIX + "com/stormpath/sdk/servlet/config/web." + STORMPATH_PROPERTIES + NL +
                    ClasspathResource.SCHEME_PREFIX + STORMPATH_PROPERTIES + NL +
                    "/WEB-INF/stormpath.properties" + NL +
                    INTERNAL_TOKEN + NL +
                    CONTEXT_PARAM_TOKEN + NL +
                    ENVVARS_TOKEN + NL +
                    SYSPROPS_TOKEN;

    @Override
    public Config createConfig(ServletContext servletContext) {

        Map<String,String> props = new LinkedHashMap<String, String>();

        for(PropertiesSource source : getPropertySources(servletContext)) {
            Map<String,String> srcProps = source.getProperties();
            props.putAll(srcProps);
        }

        return new DefaultConfig(servletContext, props);
    }

    protected Collection<PropertiesSource> getPropertySources(ServletContext servletContext) {
        ResourceFactory resourceFactory = new ServletContainerResourceFactory(servletContext);

        String sourceDefs = servletContext.getInitParameter(STORMPATH_PROPERTIES_SOURCES);
        if (!Strings.hasText(sourceDefs)) {
            sourceDefs = DEFAULT_STORMPATH_PROPERTIES_SOURCES;
        }

        Collection<PropertiesSource> sources = new ArrayList<PropertiesSource>();

        Scanner scanner = new Scanner(sourceDefs);

        while (scanner.hasNextLine()) {

            String line = scanner.nextLine();
            line = Strings.trimWhitespace(line);

            boolean required = false;

            int i = line.lastIndexOf(REQUIRED_TOKEN);
            if (i > 0) {
                required = true;
                line = line.substring(0, i);
                line = Strings.trimWhitespace(line);
            }

            if (ENVVARS_TOKEN.equalsIgnoreCase(line)) {
                sources.add(buildEnvironmentPropertiesSource());
            }
            else if (SYSPROPS_TOKEN.equalsIgnoreCase(line)) {
                sources.add(buildSystemPropertiesSource());
            }
            else if (CONTEXT_PARAM_TOKEN.equalsIgnoreCase(line)) {
                String value = servletContext.getInitParameter(STORMPATH_PROPERTIES);
                if (Strings.hasText(value)) {
                    sources.add(new ResourcePropertiesSource(new StringResource(value)));
                }
            }
            else if (INTERNAL_TOKEN.equalsIgnoreCase(line)){
                sources.addAll(getInternalPropertiesSources(servletContext));
            }
            else {
                Resource resource = resourceFactory.createResource(line);
                sources.add(buildResourcePropertiesSource(resource, required));

                // look for JSON and YAML files with the same name
                if (line.contains(".properties")) {

                    String jsonFile = line.replace(".properties", ".json");
                    resource = resourceFactory.createResource(jsonFile);
                    sources.add(wrapIfRequired(new JSONPropertiesSource(resource), required));

                    String yamlFile = line.replace(".properties", ".yaml");
                    resource = resourceFactory.createResource(yamlFile);
                    sources.add(wrapIfRequired(new YAMLPropertiesSource(resource), required));

                    yamlFile = line.replace(".properties", ".yml");
                    resource = resourceFactory.createResource(yamlFile);
                    sources.add(wrapIfRequired(new YAMLPropertiesSource(resource), required));
                }
            }
        }
        return sources;
    }

    private PropertiesSource buildEnvironmentPropertiesSource() {
        return new FilteredPropertiesSource(
                new EnvironmentVariablesPropertiesSource(),
                new FilteredPropertiesSource.Filter() {
                    @Override
                    public String[] map(String key, String value) {
                        if (key.startsWith("STORMPATH_")) {
                            //we want to convert env var naming convention to dotted property convention
                            //to allow overrides.  Overrides work based on overriding identically-named keys:
                            key = envVarNameConverter.toDottedPropertyName(key);
                            return new String[]{key, value};
                        }
                        return null;
                    }
                });
    }

    private PropertiesSource buildSystemPropertiesSource() {
        return new FilteredPropertiesSource(
                new SystemPropertiesSource(),
                new FilteredPropertiesSource.Filter() {
                    @Override
                    public String[] map(String key, String value) {
                        if (key.startsWith("stormpath.")) {
                            return new String[]{key, value};
                        }
                        return null;
                    }
                });
    }

    private PropertiesSource buildResourcePropertiesSource(Resource resource, boolean required) {

        return wrapIfRequired(new ResourcePropertiesSource(resource), required);
    }

    private PropertiesSource wrapIfRequired(PropertiesSource propertiesSource, boolean required) {

        if (!required) {
            propertiesSource = new OptionalPropertiesSource(propertiesSource);
        }
        return propertiesSource;
    }

    protected Collection<PropertiesSource> getInternalPropertiesSources(ServletContext servletContext) {
        return Collections.emptySet();
    }
}
