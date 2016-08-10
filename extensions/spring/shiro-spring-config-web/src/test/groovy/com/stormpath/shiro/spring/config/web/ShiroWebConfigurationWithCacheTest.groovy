package com.stormpath.shiro.spring.config.web

import com.stormpath.shiro.spring.config.ShiroAnnotationProcessorConfiguration
import org.apache.shiro.mgt.SecurityManager
import org.apache.shiro.realm.text.TextConfigurationRealm
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests
import org.testng.annotations.Test

import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.*

@ContextConfiguration(classes = [RealmConfiguration, CacheManagerConfiguration, ShiroWebConfiguration, ShiroAnnotationProcessorConfiguration])
public class ShiroWebConfigurationWithCacheTest extends AbstractTestNGSpringContextTests {

    @Autowired
    private SecurityManager securityManager

    @Autowired
    private AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor

    @Test
    public void testMinimalConfiguration() {

        // first do a quick check of the injected objects
        org.testng.Assert.assertNotNull authorizationAttributeSourceAdvisor
        org.testng.Assert.assertNotNull securityManager
        org.testng.Assert.assertSame securityManager, authorizationAttributeSourceAdvisor.securityManager
        assertThat securityManager.realms, allOf(hasSize(1), hasItem(instanceOf(TextConfigurationRealm)))
        org.testng.Assert.assertNotNull securityManager.cacheManager

//        // now lets do a couple quick permission tests to make sure everything has been initialized correctly.
//        Subject joeCoder = new Subject.Builder(securityManager).buildSubject()
//        joeCoder.login(new UsernamePasswordToken("joe.coder", "password"))
//        joeCoder.checkPermission("read")
//        org.testng.Assert.assertTrue joeCoder.hasRole("user")
//        org.testng.Assert.assertFalse joeCoder.hasRole("admin")
//        joeCoder.logout()
    }

}
