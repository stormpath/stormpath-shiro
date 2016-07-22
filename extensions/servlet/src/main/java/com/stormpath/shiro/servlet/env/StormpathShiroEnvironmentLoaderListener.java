package com.stormpath.shiro.servlet.env;

import org.apache.shiro.util.StringUtils;
import org.apache.shiro.web.env.EnvironmentLoader;
import org.apache.shiro.web.env.EnvironmentLoaderListener;
import org.apache.shiro.web.env.WebEnvironment;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Bootstrap listener to startup and shutdown the web application's Shiro
 * {@link WebEnvironment} at ServletContext startup and shutdown respectively.  This class exists only to
 * implement the {@link ServletContextListener} interface. All 'real' logic is done in the parent
 * {@link EnvironmentLoader} class.
 * <h2>Usage</h2>
 * Define the following in {@code web.xml}:
 * <pre>
 * &lt;listener&gt;
 *     &lt;listener-class&gt;<code>com.stormpath.shiro.web.servlet.env.StormpathShiroEnvironmentLoaderListener</code>&lt;/listener-class&gt;
 * &lt;/listener&gt;
 * </pre>
 * Configuration options, such as the {@code WebEnvironment} class to instantiate as well as Shiro configuration
 * resource locations are specified as {@code ServletContext} {@code context-param}s and are documented in the
 * {@link EnvironmentLoader} JavaDoc.
 * <h2>Shiro Filter</h2>
 * This listener is almost always defined in conjunction with the
 * {@link org.apache.shiro.web.servlet.ShiroFilter ShiroFilter} to ensure security operations for web requests.  Please
 * see the {@link org.apache.shiro.web.servlet.ShiroFilter ShiroFilter} JavaDoc for more.
 * <BR/><BR/>
 * In addition this {@link ServletContextListener} will set the default Shiro environment class to {@link StormpathShiroIniEnvironment}
 * if the <code>shiroEnvironmentClass</code> init parameter is not set.
 *
 * @see EnvironmentLoader
 * @see org.apache.shiro.web.servlet.ShiroFilter ShiroFilter
 * @see com.stormpath.sdk.servlet.filter.StormpathFilter
 */
public class StormpathShiroEnvironmentLoaderListener extends EnvironmentLoaderListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {

        String environmentClassParam = sce.getServletContext().getInitParameter(EnvironmentLoader.ENVIRONMENT_CLASS_PARAM);
        if (!StringUtils.hasText(environmentClassParam)) {
            sce.getServletContext().setInitParameter(EnvironmentLoader.ENVIRONMENT_CLASS_PARAM, StormpathShiroIniEnvironment.class.getName());
        }

        super.contextInitialized(sce);
    }
}
