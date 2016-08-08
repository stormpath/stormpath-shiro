package com.stormpath.shiro.servlet.mvc

import com.stormpath.sdk.servlet.form.Form
import com.stormpath.shiro.servlet.ShiroTestSupport
import org.apache.shiro.SecurityUtils
import org.apache.shiro.authc.AuthenticationException
import org.apache.shiro.authc.AuthenticationToken
import org.apache.shiro.authc.UsernamePasswordToken
import org.apache.shiro.mgt.SecurityManager
import org.apache.shiro.subject.Subject
import org.apache.shiro.subject.SubjectContext
import org.easymock.Capture
import org.testng.annotations.Test

import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import static org.easymock.EasyMock.*
import static org.testng.Assert.*

/**
 * Tests for {@link ShiroLoginController}
 */
@Test(singleThreaded = true)
public class ShiroLoginControllerTest extends ShiroTestSupport {

    @Test
    public void testLoginSuccess() {

        def password = "password"
        def username = "username"
        def remoteHost = "remote.host"

        def securityManager = createMock(SecurityManager)
        def subject = createMock(Subject)

        def request = createMock(HttpServletRequest)
        def response = createMock(HttpServletResponse)
        def form = createMock(Form)

        Capture<UsernamePasswordToken> loginCapture = new Capture<>()

        expect(request.getRemoteHost()).andReturn(remoteHost)
        expect(request.getParameter("next")).andReturn(null)

        expect(form.getFieldValue("login")).andReturn username
        expect(form.getFieldValue("password")).andReturn password

        expect(securityManager.createSubject(anyObject(SubjectContext))).andReturn(subject)
        subject.login(capture(loginCapture))

        replay request, response, form, securityManager, subject

        SecurityUtils.setSecurityManager(securityManager)

        ShiroLoginController controller = new ShiroLoginController()
        controller.onValidSubmit(request, response, form)

        verify request, response, form, securityManager, subject

        def actualUsernamePasswordToken = loginCapture.value
        assertEquals username, actualUsernamePasswordToken.principal
        assertEquals password, new String(actualUsernamePasswordToken.password)
        assertEquals remoteHost, actualUsernamePasswordToken.host
        assertFalse actualUsernamePasswordToken.rememberMe

    }

    @Test
    public void testLoginFailure() {

        def password = "password"
        def username = "username"
        def remoteHost = "remote.host"

        def securityManager = createMock(SecurityManager)
        def subject = createMock(Subject)

        def request = createMock(HttpServletRequest)
        def response = createMock(HttpServletResponse)
        def form = createMock(Form)

        expect(request.getRemoteHost()).andReturn(remoteHost)

        expect(form.getFieldValue("login")).andReturn username
        expect(form.getFieldValue("password")).andReturn password

        expect(securityManager.createSubject(anyObject(SubjectContext))).andReturn(subject)
        expect(subject.login(anyObject(AuthenticationToken))).andThrow(new AuthenticationException("Expected test exception"))

        replay request, response, form, securityManager, subject

        SecurityUtils.setSecurityManager(securityManager)

        ShiroLoginController controller = new ShiroLoginController()

        try {
            controller.onValidSubmit(request, response, form)
            fail("expected ServletException")
        }
        catch(ServletException e) {
            // expected
        }
        verify request, response, form, securityManager, subject
    }
}
