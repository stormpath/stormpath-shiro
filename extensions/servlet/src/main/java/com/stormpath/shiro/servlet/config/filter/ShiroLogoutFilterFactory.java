package com.stormpath.shiro.servlet.config.filter;

import com.stormpath.sdk.servlet.config.filter.LogoutFilterFactory;
import com.stormpath.sdk.servlet.mvc.LogoutController;
import com.stormpath.shiro.servlet.mvc.ShiroLogoutController;

public class ShiroLogoutFilterFactory extends LogoutFilterFactory {

    @Override
    protected LogoutController newController() {
        return new ShiroLogoutController();
    }
}
