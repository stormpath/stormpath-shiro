package com.stormpath.shiro.spring.boot.autoconfigure

import com.stormpath.sdk.application.Application
import com.stormpath.sdk.client.Client
import com.stormpath.shiro.realm.ApplicationRealm
import org.apache.shiro.mgt.SecurityManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests
import org.testng.annotations.Test

import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.*
import static org.testng.Assert.assertNotNull

@SpringBootTest(classes = [StormpathShiroAutoConfigurationTestApplication])
public class StormpathShiroSpringAutoConfigurationTest extends AbstractTestNGSpringContextTests {

    @Autowired
    private SecurityManager securityManager

    @Autowired
    Client client

    @Autowired
    Application application

    @Test
    public void testMinimalConfiguration() {

        assertNotNull securityManager
        assertNotNull client
        assertNotNull application

        assertThat securityManager.realms, allOf(hasSize(1), hasItem(instanceOf(ApplicationRealm)))
    }
}
