package com.stormpath.shiro.spring.config.web

import com.stormpath.shiro.spring.config.ShiroAnnotationProcessorConfiguration
import org.apache.shiro.mgt.SecurityManager
import org.apache.shiro.realm.text.TextConfigurationRealm
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor
import org.apache.shiro.web.mgt.WebSecurityManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests
import org.testng.annotations.Test

import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.*
import static org.testng.Assert.*

@ContextConfiguration(classes = [RealmConfiguration, ShiroWebConfiguration, ShiroAnnotationProcessorConfiguration])
public class ShiroWebConfigurationTest extends AbstractTestNGSpringContextTests {

    @Autowired
    private SecurityManager securityManager

    @Autowired
    private AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor

    @Test
    public void testMinimalConfiguration() {

        // first do a quick check of the injected objects
        assertNotNull authorizationAttributeSourceAdvisor
        assertNotNull securityManager
        assertThat securityManager, instanceOf(WebSecurityManager)
        assertSame securityManager, authorizationAttributeSourceAdvisor.securityManager
        assertThat securityManager.realms, allOf(hasSize(1), hasItem(instanceOf(TextConfigurationRealm)))
        assertNull securityManager.cacheManager

//        // now lets do a couple quick permission tests to make sure everything has been initialized correctly.
//        Subject joeCoder = new Subject.Builder(securityManager).buildSubject()
//        joeCoder.login(new UsernamePasswordToken("joe.coder", "password"))
//        joeCoder.checkPermission("read")
//        assertTrue joeCoder.hasRole("user")
//        assertFalse joeCoder.hasRole("admin")
//        joeCoder.logout()
    }

}
