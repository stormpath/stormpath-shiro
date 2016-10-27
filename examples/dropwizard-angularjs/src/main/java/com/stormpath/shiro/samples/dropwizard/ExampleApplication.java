package com.stormpath.shiro.samples.dropwizard;

import com.stormpath.sdk.lang.Collections;
import com.stormpath.shiro.samples.dropwizard.bundle.rewrite.RewriteBundle;
import com.stormpath.shiro.samples.dropwizard.bundle.shiro.StormpathShiroBundle;
import com.stormpath.shiro.samples.dropwizard.bundle.shiro.ShiroConfiguration;
import com.stormpath.shiro.samples.dropwizard.resources.ProfileResource;
import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.bundles.webjars.WebJarBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.ocpsoft.logging.Logger;
import org.ocpsoft.rewrite.config.Condition;
import org.ocpsoft.rewrite.config.Configuration;
import org.ocpsoft.rewrite.config.ConfigurationBuilder;
import org.ocpsoft.rewrite.config.Direction;
import org.ocpsoft.rewrite.config.Log;
import org.ocpsoft.rewrite.config.Or;
import org.ocpsoft.rewrite.servlet.config.Forward;
import org.ocpsoft.rewrite.servlet.config.Path;

import java.util.HashSet;
import java.util.Set;

/**
 *  A simple Stormpath + Shiro + Dropwizard application that uses Angularjs for frontend.
 */
@SuppressWarnings("PMD")
public class ExampleApplication extends Application<ExampleConfiguration> {

    public static void main(final String[] args) throws Exception {
        new ExampleApplication().run(args);
    }

    @Override
    public String getName() {
        return "Stormpath-Shiro-Dropwizard-Angularjs-Example";
    }

    @Override
    public void initialize(final Bootstrap<ExampleConfiguration> bootstrap) {

        // Shiro filter, and Stormpath config
        bootstrap.addBundle(new StormpathShiroBundle<ExampleConfiguration>() {
            @Override
            protected ShiroConfiguration narrow(ExampleConfiguration configuration) {
                return configuration.shiro;
            }
        });

        // web jars and assets
        bootstrap.addBundle(new WebJarBundle("org.webjars", "org.webjars.bower"));
        bootstrap.addBundle(new AssetsBundle("/assets/", "/", "index.html"));

        // rewrite config.angularjsRoutes to index.html
        bootstrap.addBundle(new RewriteBundle<ExampleConfiguration>() {
            @Override
            protected Configuration narrow(ExampleConfiguration appConfiguration) {

                if (appConfiguration != null && !Collections.isEmpty(appConfiguration.angularjsRoutes) ) {
                    Set<String> paths = new HashSet<>(appConfiguration.angularjsRoutes);
                    return ConfigurationBuilder.begin()
                            .addRule()
                            .when(
                                Direction.isInbound()
                                .and(Path.matches("/{path}"))
                                .and(matchesAnyOfPaths(paths))
                            )
                            .perform(
                                Log.message(Logger.Level.DEBUG, "Forwarding to index.html from {path}")
                                .and(Forward.to("/index.html"))
                            );
                }
                return null;
            }

            private Condition matchesAnyOfPaths(Set<String> paths) {
                Set<Path> pathSet = new HashSet<>();
                for (String path : paths) {
                    pathSet.add(Path.matches(path));
                }
                Path[] result = Collections.toArray(pathSet, Path.class);
                return Or.any(result);
            }
        });
    }

    @Override
    public void run(final ExampleConfiguration configuration,
                    final Environment environment) {

        // JAX-RS are all under /api
        environment.jersey().setUrlPattern("/api/*");

        // Register each JAX-RS resoruce
        environment.jersey().register(ProfileResource.class);
    }
}
