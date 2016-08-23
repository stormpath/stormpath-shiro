package com.stormpath.shiro.examples;


import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.config.IniSecurityManagerFactory;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.Factory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Quickstart {



    private static final transient Logger log = LoggerFactory.getLogger(Quickstart.class);


    @SuppressWarnings("Duplicates")
    public static void main(String[] args) {

        // The easiest way to create a Shiro SecurityManager with configured
        // realms, users, roles and permissions is to use the simple INI config.
        // We'll do that by using a factory that can ingest a .ini file and
        // return a SecurityManager instance:

        // Use the shiro.ini file at the root of the classpath
        // (file: and url: prefixes load from files and urls respectively):
        Factory<SecurityManager> factory = new IniSecurityManagerFactory("classpath:shiro.ini");
        SecurityManager securityManager = factory.getInstance();

        // for this simple example quickstart, make the SecurityManager
        // accessible as a JVM singleton.  Most applications wouldn't do this
        // and instead rely on their container configuration or web.xml for
        // webapps.  That is outside the scope of this simple quickstart, so
        // we'll just do the bare minimum so you can continue to get a feel
        // for things.
        SecurityUtils.setSecurityManager(securityManager);

        // get the currently executing user:
        Subject currentUser = SecurityUtils.getSubject();

        // At this point is it not possible to provide an example user.
        // If you are new to Shiro please take a look at this example:
        // https://github.com/apache/shiro/blob/1.3.x/samples/quickstart/src/main/java/Quickstart.java
        // for details on basic usage.

        // We know the user is not authenticated yet, but this is how you would check.
        if (!currentUser.isAuthenticated()) {
            // If you want following code to succeed, you will need to create a user with id 'lonestarr'
            // and password 'vespa' in your application.
            String username = "lonestarr";
            UsernamePasswordToken token = new UsernamePasswordToken(username, "vespa");

            try {
                currentUser.login(token);
            }
            catch (AuthenticationException ae) {
                log.info("Login for user [{}] failed.", username);
            }
        }
    }

}
