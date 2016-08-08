package com.stormpath.shiro.servlet.mvc;

import com.stormpath.sdk.servlet.mvc.LogoutController;
import com.stormpath.sdk.servlet.mvc.ViewModel;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.SessionException;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ShiroLogoutController extends LogoutController {

    private static final Logger log = LoggerFactory.getLogger(ShiroLogoutController.class);

    @Override
    public ViewModel doPost(HttpServletRequest request, HttpServletResponse response) throws Exception {

        Subject subject = SecurityUtils.getSubject();
        try {
            subject.logout();
        } catch (SessionException ise) {
            log.debug("Encountered session exception during logout.  This can generally safely be ignored.", ise);
        }

        return super.doPost(request, response);
    }
}
