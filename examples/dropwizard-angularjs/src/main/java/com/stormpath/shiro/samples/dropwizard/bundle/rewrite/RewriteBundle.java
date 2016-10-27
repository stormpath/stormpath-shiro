package com.stormpath.shiro.samples.dropwizard.bundle.rewrite;

import io.dropwizard.ConfiguredBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.ocpsoft.rewrite.config.Configuration;
import org.ocpsoft.rewrite.servlet.RewriteFilter;
import org.ocpsoft.rewrite.servlet.impl.RewriteServletContextListener;
import org.ocpsoft.rewrite.servlet.impl.RewriteServletRequestListener;

import javax.servlet.DispatcherType;
import java.util.EnumSet;

import static com.stormpath.shiro.samples.dropwizard.bundle.rewrite.ServletContextConfigurationProvider.REWRITE_CONFIG_KEY;
import static org.ocpsoft.rewrite.annotation.config.AnnotationConfigProvider.CONFIG_BASE_PACKAGES;

/**
 * Configures rewrite-servlet in a Dropwizard environment.  (includes listeners {@link RewriteServletRequestListener}
 * and {@link RewriteServletContextListener} as well registering the {@link RewriteFilter}).
 * @param <T>
 */
public abstract class RewriteBundle<T> implements ConfiguredBundle<T> {

    @SuppressWarnings("PMD.EmptyMethodInAbstractClassShouldBeAbstract")
    @Override
    public void initialize(Bootstrap<?> bootstrap) {
        // do nothing
    }

    /**
     * Returns a rewrite-servlet {@link Configuration} from an application specific Dropwizard configuration.
     * @param appConfiguration Application specific configuration.
     * @return Returns a rewrite-servlet {@link Configuration} from an application specific Dropwizard configuration.
     */
    protected abstract Configuration narrow(T appConfiguration);

    @Override
    public void run(T configuration, Environment environment) throws Exception {

        // disable annotation scanning
        environment.servlets().setInitParameter(CONFIG_BASE_PACKAGES, "none");

        // set the config attribute
        environment.getApplicationContext().setAttribute(REWRITE_CONFIG_KEY, narrow(configuration));

        environment.servlets().addServletListeners(new RewriteServletRequestListener(), new RewriteServletContextListener());

        // load the filter
        environment.servlets().addFilter("rewrite", RewriteFilter.class) .addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), false, "/*");

    }
}
