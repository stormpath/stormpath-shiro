package com.stormpath.shiro.spring.boot.autoconfigure

import org.apache.shiro.authc.UsernamePasswordToken
import org.apache.shiro.mgt.SecurityManager
import org.apache.shiro.subject.Subject
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests
import org.testng.annotations.Test

import static org.testng.Assert.*

@SpringBootTest(classes = [ShiroAutoConfigurationTestApplication])
public class ShiroSpringAutoConfigurationTest extends AbstractTestNGSpringContextTests {

    @Autowired
    private SecurityManager securityManager

    @Test
    public void testMinimalConfiguration() {

        // first do a quick check of the injected objects
        assertNotNull securityManager

        // now lets do a couple quick permission tests to make sure everything has been initialized correctly.
        Subject joeCoder = new Subject.Builder(securityManager).buildSubject()
        joeCoder.login(new UsernamePasswordToken("joe.coder", "password"))
        joeCoder.checkPermission("read")
        assertTrue joeCoder.hasRole("user")
        assertFalse joeCoder.hasRole("admin")
        joeCoder.logout()
    }


}
